package com.imperium.ims.common.enums;

/**
 * Enumeration of all status values shared across multiple modules.
 */
public enum Status {

    /** Entity is active and operational. */
    ACTIVE,

    /** Entity is inactive / suspended. */
    INACTIVE,

    /** Entity has been soft-deleted. */
    DELETED,

    /** Entity is pending review or activation. */
    PENDING,

    /** Entity has been archived (read-only). */
    ARCHIVED,

    /** Entity is pending email verification. */
    PENDING_VERIFICATION,

    /** Account is temporarily blocked (e.g., too many failed logins). */
    BLOCKED
}
