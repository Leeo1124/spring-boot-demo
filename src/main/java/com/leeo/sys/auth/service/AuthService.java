package com.leeo.sys.auth.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leeo.common.repository.BaseRepository;
import com.leeo.common.service.BaseService;
import com.leeo.sys.auth.entity.Auth;
import com.leeo.sys.auth.repository.AuthRepository;

@Service
@Transactional
public class AuthService extends BaseService<Auth, Long> {

    private AuthRepository authRepository;

    @Autowired
    @Override
    public void setRepository(BaseRepository<Auth, Long> authRepository) {
        this.baseRepository = authRepository;
        this.authRepository = (AuthRepository) authRepository;
    }

    @Transactional(readOnly = true)
    public Set<Long> findRoleIds(Long userId) {
        return this.authRepository.findRoleIds(userId);
    }

    @Transactional(readOnly = true)
    public Set<Long> findRoleIds(Set<Long> groupIds) {
        return this.authRepository.findRoleIds(groupIds);
    }

    /**
     * 根据用户信息获取 角色
     * 1.1、用户  根据用户绝对匹配
     * 1.2、组织机构 根据组织机构绝对匹配 此处需要注意 祖先需要自己获取
     * 1.3、工作职务 根据工作职务绝对匹配 此处需要注意 祖先需要自己获取
     * 1.4、组织机构和工作职务  根据组织机构和工作职务绝对匹配 此处不匹配祖先
     * 1.5、组  根据组绝对匹配
     *
     * @param userId             必须有
     * @param groupIds           可选
     * @param organizationIds    可选
     * @return
     */
    @Transactional(readOnly = true)
    public Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organizationIds) {
        return this.authRepository.findRoleIds(userId, groupIds, organizationIds);
    }
}