package com.leeo.sys.organization.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Sets;
import com.leeo.common.repository.BaseRepository;
import com.leeo.common.service.BaseService;
import com.leeo.sys.organization.entity.Organization;
import com.leeo.sys.organization.repository.OrganizationRepository;

@Service
@Transactional
public class OrganizationService extends BaseService<Organization, Long> {

	private OrganizationRepository organizationRepository;

	@Autowired
	@Override
	public void setRepository(BaseRepository<Organization, Long> organizationRepository) {
		this.baseRepository = organizationRepository;
		this.organizationRepository = (OrganizationRepository) organizationRepository;
	}

	/**
	 * 过滤仅获取可显示的数据
	 *
	 * @param organizationIds
	 */
	public void filterForCanShow(Set<Long> organizationIds) {

		Iterator<Long> iter1 = organizationIds.iterator();

		while (iter1.hasNext()) {
			Long id = iter1.next();
			Organization o = findOne(id);
			if (o == null || Boolean.FALSE.equals(o.getShow())) {
				iter1.remove();
			}
		}
	}
	
	//----------------------------------
	public Set<Long> findAncestorIds(Iterable<Long> currentIds) {
        Set<Long> parents = Sets.newHashSet();
        for (Long currentId : currentIds) {
            parents.addAll(findAncestorIds(currentId));
        }
        return parents;
    }

    public Set<Long> findAncestorIds(Long currentId) {
        Set<Long> ids = Sets.newHashSet();
        Organization m = findOne(currentId);
        if (m == null) {
            return ids;
        }
        for (String idStr : StringUtils.tokenizeToStringArray(m.getParentIds(), "/")) {
            if (!StringUtils.isEmpty(idStr)) {
                ids.add(Long.valueOf(idStr));
            }
        }
        return ids;
    }

}
