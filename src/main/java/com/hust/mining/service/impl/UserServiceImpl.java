package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.dao.PowerDao;
import com.hust.mining.dao.RolePowerDao;
import com.hust.mining.dao.UserDao;
import com.hust.mining.model.Power;
import com.hust.mining.model.RolePower;
import com.hust.mining.model.User;
import com.hust.mining.model.params.UserQueryCondition;
import com.hust.mining.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private RolePowerDao rolePowerDao;
	@Autowired
	private PowerDao powerDao;

	@Override
	public List<User> selectAllUserInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String loginName = (String) session.getAttribute("username");
		List<User> users = userDao.selectByUserName(loginName);
		List<User> user = new ArrayList<>();
		int roleId = users.get(0).getRoleId();
		System.out.println(roleId);
		if (roleId == 1 || roleId == 2) {
			user = userDao.selectAllUserInfo();
		} else {
			user = userDao.selectByUserName(loginName);
		}
		return user;
	}

	@Override
	public List<User> selectSingleUserInfo(String userName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String loginName = (String) session.getAttribute("username");
		List<User> users = userDao.selectByUserName(loginName);
		List<User> user = new ArrayList<>();
		int roleId = users.get(0).getRoleId();
		if (roleId != 1 && roleId != 2) {
			logger.info("only select youself");
			user = userDao.selectByUserName(loginName);
		} else {
			user = userDao.selectByLikeUserName(userName);
		}
		return user;
	}

	@Override
	public boolean deleteUserInfoById(int userId, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String loginName = (String) session.getAttribute("username");
		List<User> user = userDao.selectByUserName(loginName);
		int roleId = user.get(0).getRoleId();
		User users = userDao.selectByPrimaryKey(userId);
		if (roleId != 1 && users.getRoleId() == 1) {
			logger.info("can not delete root");
			return false;
		}
		int statue = userDao.deleteByPrimaryKey(userId);
		if (statue == 0) {
			logger.info("delete userinfo error ");
			return false;
		}
		return true;
	}

	@Override
	public boolean updateUserInfo(User user) {
		int statue = userDao.updateByPrimaryKeySelective(user);
		if (statue == 0) {
			logger.info("updateUserInfo is error");
			return false;
		}
		return true;
	}

	@Override
	public boolean insertUserInfo(User user) {
		int statue = userDao.insert(user);
		if (statue == 0) {
			logger.info("insert user error ");
			return false;
		}
		return true;
	}

	@Override
	public boolean login(String userName, String password) {
		List<User> users = userDao.checkLogin(userName, password);
		if (null == users || users.size() == 0) {
			logger.info("username or password is incorrect");
			return false;
		}
		return true;
	}

	@Override
	public List<String> selectUserPowerUrl(String userName) {
		List<String> powerUrls = new ArrayList<>();
		List<Integer> powerId = new ArrayList<>();
		List<User> users = userDao.selectByUserName(userName);
		List<RolePower> rolePowers = new ArrayList<>();
		if (null != users.get(0).getRoleId()) {
			rolePowers = rolePowerDao.selectRolePowerByRoleId(users.get(0).getRoleId());
		} else {
			logger.info("power url is empty");
			return powerUrls;
		}
		for (RolePower rolePowerInfo : rolePowers) {
			powerId.add(rolePowerInfo.getPowerId());
		}
		List<Power> power = powerDao.selectPowerByPOwerId(powerId);
		for (Power powerInfo : power) {
			powerUrls.add(powerInfo.getPowerUrl());
		}
		return powerUrls;
	}

	@Override
	public long countOfUser() {
		long numbers = userDao.selectCountOfUser();
		return numbers;
	}

	@Override
	public List<User> selectUserByPageLimit(UserQueryCondition userQueryCondition) {
		List<User> users = userDao.selectByExample(userQueryCondition);
		return users;
	}

	@Override
	public String getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (null == session) {
			return null;
		}
		return (String) session.getAttribute("username");
	}

	@Override
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (null == session) {
			return;
		}
		if (null != session.getAttribute("username")) {
			session.removeAttribute("username");
		}

	}

}
