package com.leeo.sys.user.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leeo.common.repository.BaseRepository;
import com.leeo.common.service.BaseService;
import com.leeo.common.utils.UserLogUtils;
import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.entity.UserStatus;
import com.leeo.sys.user.exception.UserBlockedException;
import com.leeo.sys.user.exception.UserNotExistsException;
import com.leeo.sys.user.exception.UserPasswordNotMatchException;
import com.leeo.sys.user.repository.UserRepository;


@Service
@Transactional
public class UserService extends BaseService<User, Long> {

    private UserRepository userRepository;
    @Autowired
    private PasswordService passwordService;
    
    @Autowired
    @Override
    public void setRepository(BaseRepository<User, Long> userRepository) {
        this.baseRepository = userRepository;
        this.userRepository = (UserRepository) userRepository;
    }

    public User findByUsername(String username) {
        if(StringUtils.isEmpty(username)) {
            return null;
        }
        return this.userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        if(StringUtils.isEmpty(email)) {
            return null;
        }
        return this.userRepository.findByEmail(email);
    }


    public User findByMobilePhoneNumber(String mobilePhoneNumber) {
        if(StringUtils.isEmpty(mobilePhoneNumber)) {
            return null;
        }
        return this.userRepository.findByMobilePhoneNumber(mobilePhoneNumber);
    }
    
    public User findByLoginName(String loginName){
    	User user = null;

        if (maybeUsername(loginName)) {
            user = findByUsername(loginName);
        }

        if (user == null && maybeEmail(loginName)) {
            user = findByEmail(loginName);
        }

        if (user == null && maybeMobilePhoneNumber(loginName)) {
            user = findByMobilePhoneNumber(loginName);
        }
        
        return user;
    }


    public User changePassword(User user, String newPassword) {
        user.randomSalt();
        user.setPassword(passwordService.encryptPassword(user.getUsername(), newPassword, user.getSalt()));
        update(user);
        return user;
    }

    public User changeStatus(User opUser, User user, UserStatus newStatus, String reason) {
        user.setStatus(newStatus);
        update(user);
//        userStatusHistoryService.log(opUser, user, newStatus, reason);
        return user;
    }

    public User login(String username, String password) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "username is empty");
            throw new UserNotExistsException();
        }
        //密码如果不在指定范围内 肯定错误
        if (password.length() < User.PASSWORD_MIN_LENGTH || password.length() > User.PASSWORD_MAX_LENGTH) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "password length error! password is between {} and {}",
                    User.PASSWORD_MIN_LENGTH, User.PASSWORD_MAX_LENGTH);

            throw new UserPasswordNotMatchException();
        }

        User user = null;

        if (maybeUsername(username)) {
            user = findByUsername(username);
        }

        if (user == null && maybeEmail(username)) {
            user = findByEmail(username);
        }

        if (user == null && maybeMobilePhoneNumber(username)) {
            user = findByMobilePhoneNumber(username);
        }

        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "user is not exists!");

            throw new UserNotExistsException();
        }

//        passwordService.validate(user, password);

        if (user.getStatus() == UserStatus.blocked) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "user is blocked!");
            throw new UserBlockedException("用户已锁定");//(userStatusHistoryService.getLastReason(user));
        }

        UserLogUtils.log(
                username,
                "loginSuccess",
                "");
        return user;
    }


    private boolean maybeUsername(String username) {
        if (!username.matches(User.USERNAME_PATTERN)) {
            return false;
        }
        //如果用户名不在指定范围内也是错误的
        if (username.length() < User.USERNAME_MIN_LENGTH || username.length() > User.USERNAME_MAX_LENGTH) {
            return false;
        }

        return true;
    }

    private boolean maybeEmail(String username) {
        if (!username.matches(User.EMAIL_PATTERN)) {
            return false;
        }
        return true;
    }

    private boolean maybeMobilePhoneNumber(String username) {
        if (!username.matches(User.MOBILE_PHONE_NUMBER_PATTERN)) {
            return false;
        }
        return true;
    }

    public void changePassword(User opUser, Long[] ids, String newPassword) {
        UserService proxyUserService = (UserService) AopContext.currentProxy();
        for (Long id : ids) {
            User user = findOne(id);
            proxyUserService.changePassword(user, newPassword);
            UserLogUtils.log(
                    user.getUsername(),
                    "changePassword",
                    "admin user {} change password!", opUser.getUsername());

        }
    }

    public void changeStatus(User opUser, Long[] ids, UserStatus newStatus, String reason) {
        UserService proxyUserService = (UserService) AopContext.currentProxy();
        for (Long id : ids) {
            User user = findOne(id);
            proxyUserService.changeStatus(opUser, user, newStatus, reason);
            UserLogUtils.log(
                    user.getUsername(),
                    "changeStatus",
                    "admin user {} change status!", opUser.getUsername());
        }
    }

}