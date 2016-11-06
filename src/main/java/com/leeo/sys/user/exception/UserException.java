package com.leeo.sys.user.exception;

import com.leeo.common.exception.BaseException;

public class UserException extends BaseException {

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }

}
