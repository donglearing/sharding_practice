package com.udesk.sharding.practice.dal.mapper;

import com.udesk.sharding.practice.dal.dao.CustomerFollowUpsDO;

public interface CustomerFollowUpsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustomerFollowUpsDO record);

    int insertSelective(CustomerFollowUpsDO record);

    CustomerFollowUpsDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustomerFollowUpsDO record);

    int updateByPrimaryKeyWithBLOBs(CustomerFollowUpsDO record);

    int updateByPrimaryKey(CustomerFollowUpsDO record);
}