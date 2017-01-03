package com.hust.mining.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.dao.PowerDao;
import com.hust.mining.dao.RoleDao;
import com.hust.mining.dao.RolePowerDao;
import com.hust.mining.dao.UserDao;
import com.hust.mining.dao.UserRoleDao;
import com.hust.mining.model.Power;
import com.hust.mining.model.RolePower;
import com.hust.mining.model.User;
import com.hust.mining.model.UserRole;
import com.hust.mining.model.params.UserQueryCondition;
import com.hust.mining.redis.RedisFacade;
import com.hust.mining.service.UserService;
import com.hust.mining.util.TimeUtil;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private UserRoleDao userRoleDao;
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
		// 要根据userID 去判断找到对应的ROleid
		List<Integer> roleId = new ArrayList<>();
		List<UserRole> userRole = userRoleDao.selectAllUserRole();
		for (UserRole userRoleInfo : userRole) {
			if (userRoleInfo.getUserId() == users.get(0).getUserId()) {
				roleId.add(userRoleInfo.getRoleId());
			}
		}
		// 如果登录的用户是普通用户 只能查询自己的角色，得到的是这个登录用户的角色ID
		// 假如此用户的角色ID不是管理员和超级管理员，也就是说
		if (roleId.contains(1) || roleId.contains(2)) {
			user = userDao.selectAllUserInfo();
			return user;
		}
		user = userDao.selectByUserName(loginName);
		if (user.isEmpty()) {
			logger.info("user is empty");
			return user;
		}
		return user;
	}

	@Override
	public List<User> selectSingleUserInfo(String userName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String loginName = (String) session.getAttribute("username");
		List<User> users = userDao.selectByUserName(loginName);
		List<User> user = new ArrayList<>();
		List<Integer> roleId = new ArrayList<>();
		List<UserRole> userRole = userRoleDao.selectAllUserRole();
		for (UserRole userRoleInfo : userRole) {
			if (userRoleInfo.getUserId() == users.get(0).getUserId()) {
				roleId.add(userRoleInfo.getRoleId());
			}
		}
		if (!roleId.contains(1) || !roleId.contains(2)) {
			user = userDao.selectByUserName(loginName);
			return user;
		}
		user = userDao.selectByUserName(userName);
		if (user.isEmpty()) {
			logger.info("userName is not exist");
			return user;
		}
		return user;
	}

	@Override
	public boolean deleteUserInfoById(int userId, HttpServletRequest request) {
		List<UserRole> userRole = userRoleDao.selectUserRoleByUserId(userId);
		List<Integer> id = new ArrayList<>();
		for (UserRole userRoleInfo : userRole) {
			id.add(userRoleInfo.getId());
		}
		for (int idInfo : id) {
			int status = userRoleDao.deleteUserRoleByUserId(idInfo);
			if (status == 0) {
				logger.info("delete userRole id error");
				return false;
			}
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

	/**
	 * 添加用户 会传递过来角色名称
	 */
	@Override
	public boolean insertUserInfo(User user, List<String> roleName) {
		try {
			user.setCreateDate(TimeUtil.getSystemDate());
		} catch (ParseException e) {
			logger.info("get systemdate is error ");
			e.printStackTrace();
		}
		int statue = userDao.insert(user);
		if (statue == 0) {
			logger.info("insert user error ");
			return false;
		}
		// 上面是添加用户信息成功，现在需要取出用户ID, 然后根据角色名称取出角色ID，然后再把相应的信息添加到userRole表中
		List<User> users = userDao.selectByUserName(user.getUserName());
		int userId = users.get(0).getUserId();
		List<Integer> roleId = new ArrayList<>();
		for (String roleNameInfo : roleName) {
			roleId.add(roleDao.selectRoleByName(roleNameInfo).get(0).getRoleId());
		}
		for (int roleIdInfo : roleId) {
			UserRole userRole = new UserRole();
			userRole.setUserId(userId);
			userRole.setRoleId(roleIdInfo);
			int status = userRoleDao.insertUserRole(userRole);
			if (status == 0) {
				logger.info("insert userRole is error");
				return false;
			}
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
		// 首先用户会有很多角色 所以查询用户拥有的权限的时候，应该去根据用户名 去用户角色表中查找相关的角色Id,
		int userId = users.get(0).getUserId();
		List<Integer> roleId = new ArrayList<>();
		List<UserRole> userRole = userRoleDao.selectUserRoleByUserId(userId);
		for (UserRole userRoleInfo : userRole) {
			roleId.add(userRoleInfo.getRoleId());
		}
		System.out.println("用户有几个角色：" + roleId.size());
		// 现在得到roleId了，roleId有很多 ，不止一个
		// 也可以先把第一个元素添加进去，然后循环其他的元素，假如roleId>1说明具有很多角色，首先把第一个角色权限信息写进入，然后再循环其他元素假如到的的信息和1不同则添加进去
		// 首先要判断用户到底有几个角色把
		if (roleId.size() == 0) {
			logger.info("用户没有分配角色");
			return powerUrls;
		}
		rolePowers = rolePowerDao.selectRolePowerByRoleId(roleId.get(0));
		// 获得权限ID
		for (RolePower rolePowerInfo : rolePowers) {
			powerId.add(rolePowerInfo.getPowerId());
		}
		if (roleId.size() > 1) {
			// 说明用户具有很多个角色
			for (int i = 1; i < roleId.size(); i++) {
				List<RolePower> rolePower = rolePowerDao.selectRolePowerByRoleId(roleId.get(i));
				for (RolePower rolePowerInfo : rolePower) {
					if (!powerId.contains(rolePowerInfo.getPowerId())) {
						powerId.add(rolePowerInfo.getPowerId());
					}
				}
			}
		}
		List<Power> power = powerDao.selectPowerByPowerId(powerId);
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
	public String getCurrentUser() {
		RedisFacade redis = RedisFacade.getInstance(true);
		String username = redis.getString(KEY.USER_NAME);
		if (StringUtils.isEmpty(username)) {
			return null;
		}
		return username;
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
