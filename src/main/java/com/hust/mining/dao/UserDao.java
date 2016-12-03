package com.hust.mining.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.UserMapper;
import com.hust.mining.model.User;
import com.hust.mining.model.UserExample;
import com.hust.mining.model.UserExample.Criteria;
import com.hust.mining.model.params.UserQueryCondition;

public class UserDao {

	@Autowired
	private UserMapper userMapper;

	public List<User> selectAllUserInfo() {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdIsNotNull();
		List<User> users = userMapper.selectByExample(example);
		return users;
	}

	public List<User> selectByUserName(String userName) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameEqualTo(userName);
		List<User> users = userMapper.selectByExample(example);
		return users;
	}

	public List<User> selectByLikeUserName(String userName) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameLike(userName);
		List<User> users = userMapper.selectByExample(example);
		return users;
	}

	public List<User> selectByRoleId(int roleId) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andRoleIdEqualTo(roleId);
		List<User> user = userMapper.selectByExample(example);
		return user;
	}

	public List<User> checkLogin(String userName, String password) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameEqualTo(userName);
		criteria.andPasswordEqualTo(password);
		List<User> users = userMapper.selectByExample(example);
		return users;
	}

	public long selectCountOfUser() {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdIsNotNull();
		long count = userMapper.countByExample(example);
		return count;
	}

	public int updateByPrimaryKeySelective(User record) {
		return userMapper.updateByPrimaryKeySelective(record);
	}

	public long countByExample(UserExample example) {
		return userMapper.countByExample(example);
	}

	public int deleteByExample(UserExample example) {
		return userMapper.deleteByExample(example);
	}

	public int deleteByPrimaryKey(Integer userId) {
		return userMapper.deleteByPrimaryKey(userId);
	}

	public int insert(User record) {
		return userMapper.insert(record);
	}

	public int insertSelective(User record) {
		return userMapper.insertSelective(record);
	}

	public List<User> selectByExample(UserQueryCondition userQueryCondition) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameEqualTo(userQueryCondition.getUserName());
		criteria.andEmailEqualTo(userQueryCondition.getEmail());
		criteria.andTelphoneEqualTo(userQueryCondition.getTelphone());
		criteria.andTrueNameEqualTo(userQueryCondition.getTrueName());
		criteria.andRoleIdEqualTo(userQueryCondition.getRoleId());
		example.setPage(userQueryCondition.getPage());
		example.setRow(userQueryCondition.getRow());
		List<User> users = userMapper.selectByExample(example);
		return users;
	}

	public User selectByPrimaryKey(Integer userId) {
		return userMapper.selectByPrimaryKey(userId);
	}

	public int updateByExampleSelective(User record, UserExample example) {
		return userMapper.updateByExample(record, example);
	}

	public int updateByExample(User record, UserExample example) {
		return userMapper.updateByExample(record, example);
	}

	public int updateByPrimaryKey(User record) {
		return userMapper.updateByPrimaryKey(record);
	}

}
