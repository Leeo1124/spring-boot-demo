package com.leeo.sys.user.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.leeo.common.repository.BaseRepository;
import com.leeo.sys.user.entity.User;


@Repository
public interface UserRepository extends BaseRepository<User, Long>{
    
    @Modifying
    @Query("delete from User user where user.username=?1")
    void deleteByUserName(String username);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)// for update
    @Query("select u from User u where u.username like %:username%")
    List<User> findByUsernameQueryLike(@Param("username")String username);
    
    @Query(value="select * from sys_user where username like ?1%" ,nativeQuery=true)
    public List<User> findByNativeSql(String username); 
    
    List<User> findByUsernameLike(String username);
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    User findByMobilePhoneNumber(String mobilePhoneNumber); 
    
    Page<User> findByDeletedFalse(Pageable pageable);
}