package com.mdau.ushirika.module.benevolence.enums;

public enum EnrollmentStatus {
    /** Enrolled but $600 not yet fully paid. */
    PAYING,
    /** $600 paid; 6-month probation period in progress. */
    PROBATION,
    /** Probation complete; member may submit claims. */
    ELIGIBLE
}
