package com.leeo.sys.user.exception;

public class UserBlockedException extends UserException {
    public UserBlockedException(String reason) {
        super("user.blocked", new Object[]{reason});
    }
}
