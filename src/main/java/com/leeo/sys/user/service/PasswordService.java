package com.leeo.sys.user.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.leeo.common.utils.UserLogUtils;
import com.leeo.common.utils.security.Md5Utils;
import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.exception.UserPasswordNotMatchException;
import com.leeo.sys.user.exception.UserPasswordRetryLimitExceedException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Service
public class PasswordService {

//    @Autowired
//    private CacheManager cacheManager;

    private Cache loginRecordCache;

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount = 10;

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @PostConstruct
    public void init() {
//        loginRecordCache = cacheManager.getCache("loginRecordCache");
    }

    public void validate(User user, String password) {
        String username = user.getUsername();

        int retryCount = 0;

        Element cacheElement = loginRecordCache.get(username);
        if (cacheElement != null) {
            retryCount = (Integer) cacheElement.getObjectValue();
            if (retryCount >= maxRetryCount) {
                UserLogUtils.log(
                        username,
                        "passwordError",
                        "password error, retry limit exceed! password: {},max retry count {}",
                        password, maxRetryCount);
                throw new UserPasswordRetryLimitExceedException(maxRetryCount);
            }
        }

        if (!matches(user, password)) {
            loginRecordCache.put(new Element(username, ++retryCount));
            UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);
            throw new UserPasswordNotMatchException();
        } else {
            clearLoginRecordCache(username);
        }
    }

    public boolean matches(User user, String newPassword) {
        return user.getPassword().equals(encryptPassword(user.getUsername(), newPassword, user.getSalt()));
    }

    public void clearLoginRecordCache(String username) {
        loginRecordCache.remove(username);
    }


    public String encryptPassword(String username, String password, String salt) {
        return Md5Utils.hash(username + password + salt);
    }


    public static void main(String[] args) {
        System.out.println(new PasswordService().encryptPassword("hcy", "123456", "iY71e4d123"));
    }
}
