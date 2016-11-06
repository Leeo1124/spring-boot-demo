package com.leeo.sys.auth.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.google.common.collect.Sets;

public class AuthRepositoryImpl {
    
    @PersistenceContext  
    private EntityManager em; 
    
    public Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organizationIds) {

        boolean hasGroupIds = groupIds.size() > 0;
        boolean hasOrganizationIds = organizationIds.size() > 0;

        StringBuilder hql = new StringBuilder("select roleIds from Auth where ");
        hql.append(" (userId=:userId) ");

        if (hasGroupIds) {
            hql.append(" or ");
            hql.append(" (groupId in (:groupIds)) ");
        }

        if (hasOrganizationIds) {
            hql.append(" or ");
            hql.append(" (( organizationId in (:organizationIds) )) ");
        }

        Query q = em.createQuery(hql.toString());

        q.setParameter("userId", userId);

        if (hasGroupIds) {
            q.setParameter("groupIds", groupIds);
        }

        if (hasOrganizationIds) {
            q.setParameter("organizationIds", organizationIds);
        }

        List<Set<Long>> roleIdSets = (List<Set<Long>>) q.getResultList();

        Set<Long> roleIds = Sets.newHashSet();
        for (Set<Long> set : roleIdSets) {
            roleIds.addAll(set);
        }

        return roleIds;
    }
}