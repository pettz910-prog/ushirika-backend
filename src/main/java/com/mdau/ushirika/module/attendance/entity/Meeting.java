package com.mdau.ushirika.module.attendance.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.attendance.enums.MeetingStatus;
import com.mdau.ushirika.module.attendance.enums.MeetingType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "meetings",
    indexes = {
        @Index(name = "idx_meetings_date",   columnList = "meeting_date"),
        @Index(name = "idx_meetings_status", columnList = "status"),
        @Index(name = "idx_meetings_type",   columnList = "type")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "meeting_date", nullable = false)
    private LocalDateTime meetingDate;

    @Column(length = 300)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeetingType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MeetingStatus status = MeetingStatus.SCHEDULED;

    @Column(length = 500)
    private String notes;
}
