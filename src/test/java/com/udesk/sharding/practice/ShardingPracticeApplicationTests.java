package com.udesk.sharding.practice;

import com.udesk.sharding.practice.dal.mapper.CustomerFollowUpsMapper;
import org.apache.shardingsphere.api.hint.HintManager;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
class ShardingPracticeApplicationTests {

	@Autowired
	private CustomerFollowUpsMapper customerFollowUpsMapper;

	@Autowired
	private DataSource primaryDataSource;

	@BeforeAll
	public static void init(){
		System.setProperty("global.mysql.host","127.0.0.1");
		System.setProperty("primary.mysql.host","127.0.0.1 ");
		System.setProperty("global.mysql.username","root");
		System.setProperty("global.mysql.password","root");
		System.setProperty("primary.mysql.username","root");
		System.setProperty("primary.mysql.password","root");
		System.setProperty("server.tomcat.max-threads","40");
		System.setProperty("deploy.env","dongpf");
		System.setProperty("spring.profiles.active","dev");
	}



	@Test
	void contextLoads() {
		HintManager hintManager = HintManager.getInstance();
		hintManager.setDatabaseShardingValue(2);

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
		Assert.assertEquals("我是 sd2", customerFollowUpsMapper.selectByPrimaryKey(1l).getContent());


		hintManager.setDatabaseShardingValue(1);
//		System.out.println(customerFollowUpsMapper.selectByPrimaryKey(1l));
		Assert.assertEquals("我是 sd1", customerFollowUpsMapper.selectByPrimaryKey(1l).getContent());
	}

	@Test
	public void printSomething(){
		HashMap map1=new HashMap();
		map1.put("1", "A");
		HashMap map2 = new HashMap();
		map2.put("2", "B");
		map2.put("3", "C");
		map1.putAll(map2);
		map2.put("2", "BB");
		System.out.println(map1);
		//两个map具有重复的key
		HashMap map3=new HashMap();
		map3.put("1", "A");
		HashMap map4 = new HashMap();
		map4.put("1", "B");
		map4.put("3", "C");
		map3.putAll(map4);
		System.out.println(map3);
	}

}
