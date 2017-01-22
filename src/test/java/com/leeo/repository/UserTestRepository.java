package com.leeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leeo.sys.user.entity.User;


public interface UserTestRepository extends JpaRepository<User, Long>{
	
}