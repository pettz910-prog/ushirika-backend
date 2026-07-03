package com.mdau.ushirika.module.attendance.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.module.attendance.dto.*;
import com.mdau.ushirika.module.attendance.entity.AttendanceRecord;
import com.mdau.ushirika.module.attendance.entity.Meeting;
import com.mdau.ushirika.module.attendance.enums.AttendanceStatus;
import com.mdau.ushirika.module.attendance.enums.MeetingStatus;
import com.mdau.ushirika.module.attendance.enums.MeetingType;
import com.mdau.ushirika.module.attendance.repository.AttendanceRecordRepository;
import com.mdau.ushirika.module.attendance.repository.MeetingRepository;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.member.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserRepository userRepository;
    private final MemberProfileRepository profileRepository;

    // ── Admin: meeting CRUD ───────────────────────────────────────────────────

    @Transactional
    public MeetingDto createMeeting(CreateMeetingRequest req) {
        Meeting meeting = Meeting.builder()
                .title(req.title())
                .description(req.description())
                .meetingDate(req.meetingDate())
                .location(req.location())
                .type(req.type())
                .notes(req.notes())
                .build();
        return MeetingDto.from(meetingRepository.save(meeting));
    }

    @Transactional
    public MeetingDto updateMeeting(UUID id, UpdateMeetingRequest req) {
        Meeting meeting = findMeetingById(id);
        if (meeting.getStatus() != MeetingStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled meetings can be updated.");
        }
        if (req.title()       != null) meeting.setTitle(req.title());
        if (req.description() != null) meeting.setDescription(req.description());
        if (req.meetingDate() != null) meeting.setMeetingDate(req.meetingDate());
        if (req.location()    != null) meeting.setLocation(req.location());
        if (req.type()        != null) meeting.setType(req.type());
        if (req.notes()       != null) meeting.setNotes(req.notes());
        return MeetingDto.from(meetingRepository.save(meeting));
    }

    @Transactional
    public MeetingDto cancelMeeting(UUID id) {
        Meeting meeting = findMeetingById(id);
        if (meeting.getStatus() == MeetingStatus.COMPLETED) {
            throw new BadRequestException("Completed meetings cannot be cancelled.");
        }
        meeting.setStatus(MeetingStatus.CANCELLED);
        return MeetingDto.from(meetingRepository.save(meeting));
    }

    /**
     * Marks a meeting as completed. Any active member with no attendance record
     * is automatically marked ABSENT, then the consecutive-absence check runs
     * for each of those members.
     */
    @Transactional
    public MeetingDto completeMeeting(UUID id) {
        Meeting meeting = findMeetingById(id);
        if (meeting.getStatus() != MeetingStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled meetings can be marked as completed.");
        }
        meeting.setStatus(MeetingStatus.COMPLETED);
        meetingRepository.save(meeting);

        Set<UUID> recordedIds = attendanceRecordRepository.recordedUserIds(meeting);
        List<User> allMembers = activeMembers();

        for (User member : allMembers) {
            if (!recordedIds.contains(member.getId())) {
                AttendanceRecord record = AttendanceRecord.builder()
                        .meeting(meeting)
                        .user(member)
                        .status(AttendanceStatus.ABSENT)
                        .build();
                attendanceRecordRepository.save(record);
                if (meeting.getType() != MeetingType.SPECIAL) {
                    applyConsecutiveAbsenceRule(member);
                }
            }
        }
        return MeetingDto.from(meeting);
    }

    @Transactional(readOnly = true)
    public Page<MeetingDto> listMeetings(Pageable pageable) {
        return meetingRepository.findAllByOrderByMeetingDateDesc(pageable)
                .map(MeetingDto::from);
    }

    @Transactional(readOnly = true)
    public MeetingDto getMeeting(UUID id) {
        return MeetingDto.from(findMeetingById(id));
    }

    // ── Admin: attendance recording ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public MeetingWithAttendanceDto getMeetingWithAttendance(UUID meetingId) {
        Meeting meeting = findMeetingById(meetingId);
        List<AttendanceRecord> records = attendanceRecordRepository.findByMeeting(meeting);

        Set<UUID> recordedIds = records.stream()
                .map(r -> r.getUser().getId())
                .collect(Collectors.toSet());

        // Build memberId lookup
        Map<UUID, String> memberIdMap = buildMemberIdMap();

        List<AttendanceRecordDto> recordDtos = records.stream()
                .map(r -> AttendanceRecordDto.from(r, memberIdMap.get(r.getUser().getId())))
                .toList();

        List<UnrecordedMemberDto> unrecorded = activeMembers().stream()
                .filter(u -> !recordedIds.contains(u.getId()))
                .map(u -> new UnrecordedMemberDto(
                        u.getId(), u.getFullName(), u.getEmail(),
                        memberIdMap.get(u.getId())))
                .toList();

        return new MeetingWithAttendanceDto(MeetingDto.from(meeting), recordDtos, unrecorded);
    }

    @Transactional
    public List<AttendanceRecordDto> recordBulkAttendance(UUID meetingId, BulkAttendanceRequest req) {
        Meeting meeting = findMeetingById(meetingId);
        if (meeting.getStatus() == MeetingStatus.CANCELLED) {
            throw new BadRequestException("Cannot record attendance for a cancelled meeting.");
        }

        Map<UUID, String> memberIdMap = buildMemberIdMap();
        List<AttendanceRecordDto> results = new ArrayList<>();

        for (BulkAttendanceRequest.Entry entry : req.entries()) {
            User user = userRepository.findById(entry.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + entry.userId()));

            AttendanceRecord record = attendanceRecordRepository
                    .findByMeetingAndUser(meeting, user)
                    .orElse(AttendanceRecord.builder().meeting(meeting).user(user).build());

            record.setStatus(entry.status());
            record.setNotes(entry.notes());
            if (entry.status() == AttendanceStatus.PRESENT || entry.status() == AttendanceStatus.LATE) {
                record.setCheckedInAt(LocalDateTime.now());
            }
            attendanceRecordRepository.save(record);
            results.add(AttendanceRecordDto.from(record, memberIdMap.get(user.getId())));

            // Run consecutive absence rule only for quarterly-type meetings
            if (entry.status() == AttendanceStatus.ABSENT && meeting.getType() != MeetingType.SPECIAL) {
                applyConsecutiveAbsenceRule(user);
            }
        }
        return results;
    }

    // ── Member: attendance summary ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AttendanceSummaryDto getMyAttendanceSummary() {
        User user = currentUser();
        List<AttendanceRecord> history = attendanceRecordRepository.findByUserOrderByCreatedAtDesc(user);

        int attended = 0, absent = 0, excused = 0;
        List<AttendanceSummaryDto.MeetingItem> items = new ArrayList<>();
        for (AttendanceRecord r : history) {
            switch (r.getStatus()) {
                case PRESENT, LATE -> attended++;
                case ABSENT        -> absent++;
                case EXCUSED       -> excused++;
            }
            items.add(new AttendanceSummaryDto.MeetingItem(
                    r.getMeeting().getId(),
                    r.getMeeting().getTitle(),
                    r.getMeeting().getMeetingDate(),
                    r.getMeeting().getType().name(),
                    r.getStatus().name()
            ));
        }

        int consecutive = countConsecutiveAbsences(user);
        return new AttendanceSummaryDto(
                history.size(), attended, absent, excused,
                consecutive, consecutive == 1, user.isMembershipCeased(), items
        );
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void applyConsecutiveAbsenceRule(User user) {
        if (user.isMembershipCeased()) return;

        int consecutive = countConsecutiveAbsences(user);
        if (consecutive >= 2) {
            user.setMembershipCeased(true);
            user.setActive(false);
            userRepository.save(user);
        }
    }

    private int countConsecutiveAbsences(User user) {
        List<Meeting> recent = meetingRepository.findTop2ByTypeInAndStatusOrderByMeetingDateDesc(
                List.of(MeetingType.QUARTERLY, MeetingType.QUARTERLY_AGM),
                MeetingStatus.COMPLETED
        );
        int count = 0;
        for (Meeting m : recent) {
            AttendanceStatus status = attendanceRecordRepository
                    .findByMeetingAndUser(m, user)
                    .map(AttendanceRecord::getStatus)
                    .orElse(AttendanceStatus.ABSENT);
            if (status == AttendanceStatus.ABSENT) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private List<User> activeMembers() {
        return userRepository.findAllByRole(UserRole.MEMBER).stream()
                .filter(u -> u.isActive() && u.isEmailVerified())
                .toList();
    }

    private Map<UUID, String> buildMemberIdMap() {
        Map<UUID, String> map = new HashMap<>();
        profileRepository.findAll().forEach(p -> {
            if (p.getUser() != null && p.getMemberId() != null) {
                map.put(p.getUser().getId(), p.getMemberId());
            }
        });
        return map;
    }

    private Meeting findMeetingById(UUID id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found: " + id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
