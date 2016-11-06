package com.leeo.sys.role.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.leeo.common.repository.BaseRepository;
import com.leeo.common.service.BaseService;
import com.leeo.sys.role.entity.Role;
import com.leeo.sys.role.repository.RoleRepository;

@Service
@Transactional
public class RoleService extends BaseService<Role, Long>{
    
    private RoleRepository roleRepository;
    
    @Autowired
    @Override
    public void setRepository(BaseRepository<Role, Long> roleRepository) {
        this.baseRepository = roleRepository;
        this.roleRepository = (RoleRepository) roleRepository;
    }
    
    @Transactional(readOnly = true)
    public Page<Role> findByPage(String name, PageRequest page) {
        return this.roleRepository.findByName(name, page);
    }
    
    public Set<Role> findShowRoles(Set<Long> roleIds) {

        Set<Role> roles = Sets.newHashSet();
        List<Role> roleList = (List<Role>) this.roleRepository.findAll(roleIds);
        for (Role role : roleList) {
            if (Boolean.TRUE.equals(role.getShow())) {
                roles.add(role);
            }
        }
        
        return roles;
    }
    
}