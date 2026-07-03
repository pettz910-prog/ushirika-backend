package com.mdau.ushirika.module.attendance.enums;

public enum MeetingType {
    QUARTERLY_AGM,  // Annual General Meeting — counts for termination rule
    QUARTERLY,      // Regular quarterly meeting — counts for termination rule
    SPECIAL         // Special/emergency meeting — does NOT count for termination rule
}
