package com.poludnikiewicz.bugtracker.bug;

/**
 * P1_CRITICAL priority is of the highest importance, discussed feature is crucial for maintenance of
 * whole application, blocks development/testing or causes data loss, solution of such issue should come first, ASAP;
 * P2_IMPORTANT priority means the bug cause major damage for application flow and should be solved possibly fast;
 * P3_NORMAL priority - the bug blocks non-critical functionality and a workaround exists.
 * P4_MARGINAL priority is for bugs, that do not impede application flow, can be some cosmetic issues.
 * Solution can be delayed until some
 * unknown future release.
 * P5_REDUNDANT priority. If any complex works are needed in order to solve this issue it will likely be rejected
 * It is not obligatory for a staff member to solve such issue. minor significance, cosmetic issues, low or no impact to users
 */

public enum BugPriority {
    P1_CRITICAL,
    P2_IMPORTANT,
    P3_NORMAL,
    P4_MARGINAL,
    P5_REDUNDANT,
    UNSET;

    public static BugPriority sanitizePriorityInput(String priority) {
        priority = priority.toUpperCase();
        switch (priority) {
            case "P1":
                return BugPriority.P1_CRITICAL;
            case "CRITICAL":
                return BugPriority.P1_CRITICAL;
            case "P1_CRITICAL":
                return BugPriority.P1_CRITICAL;
            case "P2":
                return BugPriority.P2_IMPORTANT;
            case "IMPORTANT":
                return BugPriority.P2_IMPORTANT;
            case "P2_IMPORTANT":
                return BugPriority.P2_IMPORTANT;
            case "P3":
                return BugPriority.P3_NORMAL;
            case "NORMAL":
                return BugPriority.P3_NORMAL;
            case "P3_NORMAL":
                return BugPriority.P3_NORMAL;
            case "P4":
                return BugPriority.P4_MARGINAL;
            case "MARGINAL":
                return BugPriority.P4_MARGINAL;
            case "P4_MARGINAL":
                return BugPriority.P4_MARGINAL;
            case "P5":
                return BugPriority.P5_REDUNDANT;
            case "REDUNDANT":
                return BugPriority.P5_REDUNDANT;
            case "P5_REDUNDANT":
                return BugPriority.P5_REDUNDANT;
            default:
                return BugPriority.UNSET;
        }
    }

}
