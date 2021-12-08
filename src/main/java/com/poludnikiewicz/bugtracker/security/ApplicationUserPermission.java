package com.poludnikiewicz.bugtracker.security;


//TODO: probably delete this class as I do not use it!
public enum ApplicationUserPermission {

    USER_READ("user:read"),
    USER_WRITE("user:write"),
    BUG_CREATE("bug:create"),
    BUG_READ("bug:read"),
    BUG_UPDATE("bug:update"),
    BUG_DELETE("bug:delete");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
