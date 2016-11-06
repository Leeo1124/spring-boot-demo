package com.leeo.sys.group.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.leeo.common.repository.BaseRepository;
import com.leeo.common.service.BaseService;
import com.leeo.sys.group.entity.Group;
import com.leeo.sys.group.repository.GroupRepository;

@Service
@Transactional
public class GroupService extends BaseService<Group, Long> {

    private GroupRepository groupRepository;
    @Autowired
    private GroupRelationService groupRelationService;

    @Autowired
    @Override
    public void setRepository(BaseRepository<Group, Long> groupRepository) {
        this.baseRepository = groupRepository;
        this.groupRepository = (GroupRepository) groupRepository;
    }

    /**
     * 获取可用的的分组编号列表
     *
     * @param userId
     * @param organizationIds
     * @return
     */
    public Set<Long> findShowGroupIds(Long userId, Set<Long> organizationIds) {
        Set<Long> groupIds = Sets.newHashSet();
        groupIds.addAll(this.groupRepository.findDefaultGroupIds());
        groupIds.addAll(this.groupRelationService.findGroupIds(userId, organizationIds));


        //TODO 如果分组数量很多 建议此处查询时直接带着是否可用的标识去查
        for (Group group : findAll()) {
            if (Boolean.FALSE.equals(group.getShow())) {
                groupIds.remove(group.getId());
            }
        }

        return groupIds;
    }
}