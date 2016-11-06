package com.leeo.sys.user.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.leeo.sys.auth.service.AuthService;
import com.leeo.sys.group.service.GroupService;
import com.leeo.sys.organization.service.OrganizationService;
import com.leeo.sys.permission.entity.Permission;
import com.leeo.sys.permission.service.PermissionService;
import com.leeo.sys.resource.entity.Resource;
import com.leeo.sys.resource.service.ResourceService;
import com.leeo.sys.role.entity.Role;
import com.leeo.sys.role.entity.RoleResourcePermission;
import com.leeo.sys.role.service.RoleService;
import com.leeo.sys.user.entity.User;
import com.leeo.sys.user.entity.UserOrganization;

@Service
@Transactional
public class UserAuthService {
    
    @Autowired
    private GroupService groupService;
    @Autowired
    private AuthService authService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private OrganizationService organizationService;
    
    public Set<Role> findRoles(User user) {

    	if (user == null) {
            return Sets.newHashSet();
        }

        Long userId = user.getId();

        Set<Long> organizationIds = Sets.newHashSet();

        for (UserOrganization o : user.getOrganizations()) {
            Long organizationId = o.getOrganizationId();

            organizationIds.add(organizationId);
        }

        //TODO 目前默认子会继承父 后续实现添加flag控制是否继承

        //找组织机构祖先
        organizationIds.addAll(organizationService.findAncestorIds(organizationIds));

        //过滤组织机构 仅获取目前可用的组织机构数据
        organizationService.filterForCanShow(organizationIds);

        //过滤工作职务 仅获取目前可用的工作职务数据

        //默认分组 + 根据用户编号 和 组织编号 找 分组
        Set<Long> groupIds = groupService.findShowGroupIds(userId, organizationIds);

        //获取权限
        //1.1、获取用户角色
        //1.2、获取组织机构角色
        //1.3、获取工作职务角色
        //1.4、获取组织机构和工作职务组合的角色
        //1.5、获取组角色
        Set<Long> roleIds = authService.findRoleIds(userId, groupIds, organizationIds);

        Set<Role> roles = roleService.findShowRoles(roleIds);

        return roles;


    }
    
    public Set<String> findStringRoles(User user) {
        Set<Role> roles = findRoles(user);
        return Sets.newHashSet(Collections2.transform(roles, new Function<Role, String>() {
            @Override
            public String apply(Role input) {
                return input.getRole();
            }
        }));
    }
    
    public Set<String> findStringPermissions(User user) {
        Set<String> permissions = Sets.newHashSet();

        Set<Role> roles = findRoles(user);
        for (Role role : roles) {
            for (RoleResourcePermission rrp : role.getResourcePermissions()) {
                Resource resource = this.resourceService.findOne(rrp.getResourceId());

                String actualResourceIdentity = this.resourceService.findActualResourceIdentity(resource);

                //不可用 即没查到 或者标识字符串不存在
                if (resource == null || StringUtils.isEmpty(actualResourceIdentity) || Boolean.FALSE.equals(resource.getShow())) {
                    continue;
                }

                for (Long permissionId : rrp.getPermissionIds()) {
                    Permission permission = this.permissionService.findOne(permissionId);

                    //不可用
                    if (permission == null || Boolean.FALSE.equals(permission.getShow())) {
                        continue;
                    }
                    permissions.add(actualResourceIdentity + ":" + permission.getPermission());

                }
            }

        }

        return permissions;
    }
}