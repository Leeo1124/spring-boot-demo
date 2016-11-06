package com.leeo.sys.auth.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.leeo.common.repository.BaseRepository;
import com.leeo.sys.auth.entity.Auth;

// 可以不加@Repository注解
public interface AuthRepository extends BaseRepository<Auth, Long>{
    
    @Query("select roleIds from Auth where userId=?1")
    Set<Long> findRoleIds(Long userId);
    
    @Query("select roleIds from Auth where groupId in (?1) ")
    Set<Long> findRoleIds(Set<Long> groupIds);
    
    //委托给AuthRepositoryImpl实现
    public Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organizationIds);
}