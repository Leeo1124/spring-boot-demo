package com.leeo.sys.permission.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leeo.common.repository.BaseRepository;
import com.leeo.common.service.BaseService;
import com.leeo.sys.permission.entity.Permission;
import com.leeo.sys.permission.repository.PermissionRepository;

@Service
@Transactional
public class PermissionService extends BaseService<Permission, Long> {

    private PermissionRepository permissionRepository;

    @Autowired
    @Override
    public void setRepository(BaseRepository<Permission, Long> permissionRepository) {
        this.baseRepository = permissionRepository;
        this.permissionRepository = (PermissionRepository) permissionRepository;
    }

}