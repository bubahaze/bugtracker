package com.poludnikiewicz.bugtracker.security;

import com.google.common.collect.Sets;
import static com.poludnikiewicz.bugtracker.security.ApplicationUserPermission.*;

import java.util.Set;

public enum ApplicationUserRole {
    ADMIN(Sets.newHashSet(USER_READ, USER_WRITE, BUG_READ, BUG_CREATE, BUG_DELETE, BUG_UPDATE)),
    STAFF(Sets.newHashSet(USER_READ, BUG_READ, BUG_CREATE, BUG_UPDATE)),
    USER(Sets.newHashSet(BUG_CREATE, BUG_READ));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
