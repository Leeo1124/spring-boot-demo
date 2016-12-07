package com.leeo.sys.user.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Splitter;
import com.google.common.primitives.Longs;
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
    
    public Page<User> findByDeletedFalse(Pageable pageable){
    	return this.userRepository.findByDeletedFalse(pageable);
    }
    
    //动态查找
    public Page<User> findBySpec(Pageable pageable){
    	final String username = "";
    	final String ids = "";
    	final String createDate_begin = "2016-11-08";
    	final String createDate_end = "2016-11-10";
    	final String status = "normal";
    	final String admin = "0";
    	return this.userRepository.findAll(new Specification<User>() {  
            @Override  
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {  
                List<Predicate> predicates = new ArrayList<Predicate>();
                //ID
                if(StringUtils.isNotBlank(ids)){
                	List<Long> idList = Arrays.asList(StringUtils.split(ids, ',')).stream()
            				.filter(id -> NumberUtils.isNumber(id))
            				.map(id -> Long.valueOf(id))
            				.collect(Collectors.toList());
                	predicates.add(root.<Long>get("id").in(idList));
                }  
                //用户名
                if(StringUtils.isNotBlank(username)){  
                	predicates.add(cb.like(root.<String>get("username"), username+"%"));  
                } 
                //用户状态
                if(StringUtils.isNotBlank(status)){  
                	predicates.add(cb.equal(root.<Enum<?>>get("status"), EnumUtils.getEnum(UserStatus.class, status)));  
                }
                //是否是管理员
                if(StringUtils.isNotBlank(admin)){  
                	predicates.add(cb.equal(root.<Boolean>get("admin"), BooleanUtils.toBoolean(admin)));  
                } 
                //日期
                if(StringUtils.isNotBlank(createDate_begin) && StringUtils.isNotBlank(createDate_end)){  
                	Date startDate = null;
                	Date endDate = null;
					try {
						startDate = DateUtils.parseDate(createDate_begin, "yyyy-MM-dd");
						endDate = DateUtils.parseDate(createDate_end, "yyyy-MM-dd");
					} catch (ParseException e) {
						e.printStackTrace();
					}
                    predicates.add(cb.between(root.<Date>get("createDate"), startDate, endDate));  
                }  
                
                predicates.add(cb.equal(root.<Boolean>get("deleted"), 0));
                
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }  
  
        }, pageable);
    }
    
    public Page<User> findByExample(Pageable pageable){
    	User user = new User();
    	user.setUsername("hcy");
    	ExampleMatcher matcher = ExampleMatcher.matching()     
    			  .withIgnorePaths("username")
    			  .withMatcher("username", match -> match.endsWith());

    	Example<User> example = Example.of(user);
    	
    	return this.userRepository.findAll(example, pageable);
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