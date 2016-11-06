package com.leeo.sys.user.exception;


public class UserNotExistsException extends UserException {

    public UserNotExistsException() {
        super("user.not.exists", null);
    }
}
