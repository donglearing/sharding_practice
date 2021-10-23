package com.udesk.sharding.practice.contrller;

import com.udesk.sharding.practice.dal.mapper.CustomerFollowUpsMapper;
import org.apache.shardingsphere.api.hint.HintManager;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by pengfei.dong
 * Date 2021/10/21
 * Time 12:01 下午
 */
@RestController
public class ShardingContrller {

	@Autowired
	private CustomerFollowUpsMapper customerFollowUpsMapper;

	@Autowired
	private DataSource primaryDataSource;

	@GetMapping("/sharding")
	public String  hintDatasource(){
		HintManager hintManager = HintManager.getInstance();
		hintManager.setDatabaseShardingValue("primary2");

		Connection connection = null;
		try {
			connection = primaryDataSource.getConnection();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		try {
			Statement statement = connection.createStatement();
			statement.executeQuery("select 1 ");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

//		System.out.println(customerFollowUpsMapper.selectByPrimaryKey(1l));
//		Assert.assertEquals("我是 sd2", customerFollowUpsMapper.selectByPrimaryKey(1L).getContent());
		return customerFollowUpsMapper.selectByPrimaryKey(1L).getContent();
	}
}
