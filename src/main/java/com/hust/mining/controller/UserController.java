package com.hust.mining.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.model.Role;
import com.hust.mining.model.User;
import com.hust.mining.model.UserRole;
import com.hust.mining.model.params.UserQueryCondition;
import com.hust.mining.service.RoleService;
import com.hust.mining.service.UserRoleService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserRoleService userRoleService;

	/**
	 * 获取所有用户的信息 显示信息为：用户编号、用户名、角色、邮箱、电话、真实姓名
	 * 
	 * @author zhang
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectAllUser", method = RequestMethod.GET)
	public Object selectAllUserInfo(HttpServletRequest request) {
		List<User> users = userService.selectAllUserInfo(request);
		if (null == users || users.size() == 0) {
			return ResultUtil.errorWithMsg("select all user empty");
		}
		List<Role> roles = roleService.selectAllRole();
		if (null == roles || roles.size() == 0) {
			return ResultUtil.errorWithMsg("select all role empty");
		}
		List<UserRole> userRole = userRoleService.selectUserRole();
		if (userRole.isEmpty() || userRole.size() == 0) {
			return ResultUtil.errorWithMsg("select userRole empty");
		}
		Map<Object, Object> map = new HashMap<>();
		map.put("user", users);
		map.put("role", roles);
		map.put("userRole", userRole);
		return ResultUtil.success(map);
	}

	/**
	 * 查询用户：普通用户只能查询自己的个人信息、 管理员和超级管理员能查询所有人员的信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectOneUserInfo")
	public Object selectOneUserInfo(@RequestParam(value = "userName", required = true) String userName,
			HttpServletRequest request) {
		List<User> elements = userService.selectSingleUserInfo(userName, request);
		if (null == elements || elements.size() == 0) {
			return ResultUtil.errorWithMsg("user is  not exist");
		}
		return ResultUtil.success(elements);
	}

	/**
	 * 删除用户：通过用户ID删除用户;前台传递到后台需要使用用户名
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteUserInfoById")
	public Object deleteUserInfoById(@RequestParam(value = "userId", required = true) int userId,
			HttpServletRequest request) {
		boolean statue = userService.deleteUserInfoById(userId, request);
		if (statue == false) {
			return ResultUtil.errorWithMsg("delete users error");
		}
		return ResultUtil.success("delete user success");
	}

	/**
	 * 
	 * @param user
	 * @param roleName
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateUserInfo")
	public Object updateUseInfo(@RequestBody User user,
			@RequestParam(value = "roleName", required = true) List<String> roleName, HttpServletRequest request) {
		boolean statue = userService.updateUserInfo(user,roleName);
		if (statue == false) {
			return ResultUtil.errorWithMsg("update user error ");
		}
		return ResultUtil.success("update user success");
	}
	/**
	 * 添加用户信息：只能添加用户信息。不需要为用户设置角色
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insertUserInfo")
	public Object insertUserInfo(@RequestBody User user,
			@RequestParam(value = "roleName", required = true) List<String> roleName, HttpServletRequest request) {
		boolean statue = userService.insertUserInfo(user, roleName);
		if (statue == false) {
			return ResultUtil.errorWithMsg("insert userinfo erro ");
		}
		return ResultUtil.success("insert user success");
	}

	@ResponseBody
	@RequestMapping("/count")
	public Object count() {
		long numbers = userService.countOfUser();
		return numbers;
	}

	@ResponseBody
	@RequestMapping("/getUserInfoByPageLimit")
	public Object getUserInfoByPageLimit(@RequestBody UserQueryCondition userQueryCondition,
			HttpServletRequest request) {
		List<User> userInfo = userService.selectUserByPageLimit(userQueryCondition);
		List<Role> roles = roleService.selectAllRole();
		if (null == roles || roles.size() == 0) {
			return ResultUtil.errorWithMsg("select all role empty");
		}
		List<UserRole> userRole = userRoleService.selectUserRole();
		if (userRole.isEmpty() || userRole.size() == 0) {
			return ResultUtil.errorWithMsg("select userRole empty");
		}
		if (null == userInfo || userInfo.size() == 0) {
			return ResultUtil.errorWithMsg("select userInfo empty");
		}
		Map<Object, Object> map = new HashMap<>();
		map.put("userQueryCondition", userInfo);
		map.put("role", roles);
		map.put("userRole", userRole);
		return ResultUtil.success(map);
	}
}