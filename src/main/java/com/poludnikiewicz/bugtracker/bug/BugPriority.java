package com.poludnikiewicz.bugtracker.bug;

/**
 * P1_CRITICAL priority is of highest importance, discussed feature is crucial for maintenance of
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
    P5_REDUNTANT,
    UNSET

}
