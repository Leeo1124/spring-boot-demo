package com.leeo.sys.group.service;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.leeo.common.repository.BaseRepository;
import com.leeo.common.service.BaseService;
import com.leeo.sys.group.entity.GroupRelation;
import com.leeo.sys.group.repository.GroupRelationRepository;

@Service
@Transactional
public class GroupRelationService extends BaseService<GroupRelation, Long> {

	private GroupRelationRepository groupRelationRepository;

	@Autowired
	@Override
	public void setRepository(BaseRepository<GroupRelation, Long> groupRelationRepository) {
		this.baseRepository = groupRelationRepository;
		this.groupRelationRepository = (GroupRelationRepository) groupRelationRepository;
	}

	public void appendRelation(Long groupId, Long[] organizationIds) {
		if (ArrayUtils.isEmpty(organizationIds)) {
			return;
		}
		for (Long organizationId : organizationIds) {
			if (organizationId == null) {
				continue;
			}
			GroupRelation r = this.groupRelationRepository.findByGroupIdAndOrganizationId(groupId, organizationId);
			if (r == null) {
				r = new GroupRelation();
				r.setGroupId(groupId);
				r.setOrganizationId(organizationId);
				save(r);
			}
		}
	}

	public void appendRelation(Long groupId, Long[] userIds, Long[] startUserIds, Long[] endUserIds) {
		if (ArrayUtils.isEmpty(userIds) && ArrayUtils.isEmpty(startUserIds)) {
			return;
		}
		if (!ArrayUtils.isEmpty(userIds)) {
			for (Long userId : userIds) {
				if (userId == null) {
					continue;
				}
				GroupRelation r = this.groupRelationRepository.findByGroupIdAndUserId(groupId, userId);
				if (r == null) {
					r = new GroupRelation();
					r.setGroupId(groupId);
					r.setUserId(userId);
					save(r);
				}
			}
		}

		if (!ArrayUtils.isEmpty(startUserIds)) {
			for (int i = 0, l = startUserIds.length; i < l; i++) {
				Long startUserId = startUserIds[i];
				Long endUserId = endUserIds[i];
				// 范围查 如果在指定范围内 就没必要再新增一个 如当前是[10,20] 如果数据库有[9,21]
				GroupRelation r = this.groupRelationRepository
						.findByGroupIdAndStartUserIdLessThanEqualAndEndUserIdGreaterThanEqual(groupId, startUserId,
								endUserId);

				if (r == null) {
					// 删除范围内的
					this.groupRelationRepository.deleteInRange(startUserId, endUserId);
					r = new GroupRelation();
					r.setGroupId(groupId);
					r.setStartUserId(startUserId);
					r.setEndUserId(endUserId);
					save(r);
				}

			}
		}
	}

	public Set<Long> findGroupIds(Long userId, Set<Long> organizationIds) {
		if (organizationIds.isEmpty()) {
			return Sets.newHashSet(this.groupRelationRepository.findGroupIds(userId));
		}

		return Sets.newHashSet(this.groupRelationRepository.findGroupIds(userId, organizationIds));
	}

}
