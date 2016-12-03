package com.hust.mining.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.model.Power;
import com.hust.mining.model.Role;
import com.hust.mining.service.RoleService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/role")
public class RoleController {
	@Autowired
	private RoleService roleService;

	/**
	 * 查询所有的角色信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectAllRole")
	public Object selectAllRole(HttpServletRequest request) {
		List<Role> roles = roleService.selectAllRole();
		if (null == roles || roles.size() == 0) {
			return ResultUtil.errorWithMsg("select empty");
		}
		return ResultUtil.success(roles);
	}

	@ResponseBody
	@RequestMapping("/selectNotIncluedRole")
	public Object selectNotIncludeRole(@RequestParam(value = "roleId") int roleId) {
		List<Role> element = roleService.selectNotHaveRole(roleId);
		return ResultUtil.success(element);
	}

	/**
	 * 查询角色信息 ： 按照角色名称查询
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectOneRoleInfo")
	public Object selectOneRoleInfo(@RequestParam(value = "roleName", required = true) String roleName,
			HttpServletRequest request) {
		List<Role> element = roleService.selectOneRoleInfo(roleName);
		if (null == element || element.size() == 0) {
			return ResultUtil.errorWithMsg("rolename not exist");
		}
		return ResultUtil.success(element);
	}

	/**
	 * 添加角色新:只是添加角色信息 不添加权限
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insertRoleInfo")
	public Object insertRoleInfo(@RequestBody Role role, HttpServletRequest request) {
		boolean statue = roleService.insertRoleInfo(role);
		if (statue == false) {
			return ResultUtil.errorWithMsg("role table have been Role,unkow error");
		}
		return ResultUtil.success("insert roleinfo success");
	}

	/**
	 * 删除角色
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteRoleInfoById")
	public Object deleteRoleInfoById(@RequestParam(value = "roleId", required = true) int roleId,
			HttpServletRequest request) {
		boolean statue = roleService.deleteRoleInfoById(roleId);
		if (statue == false) {
			return ResultUtil.errorWithMsg("delete role error");
		}
		return ResultUtil.success("delete data success");
	}

	/**
	 * 更新角色信息
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateRoleInfo")
	public Object updateRoleInfo(@RequestBody Role role, HttpServletRequest request) {
		boolean statue = roleService.updateRoleInfo(role);
		if (statue == false) {
			return ResultUtil.errorWithMsg("update roleinfo error,unknow error");
		}
		return ResultUtil.success("update roleinfo success");
	}

	/**
	 * 为角色添加权限
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insertPowerOfRole")
	public Object insertPowerOfRole(@RequestParam(value = "roleId", required = true) int roleId,
			@RequestParam(value = "powerName", required = true) List<String> powerName, HttpServletRequest request) {
		boolean statue = roleService.insertPowerOfRole(roleId, powerName);
		if (statue == false) {
			return ResultUtil.errorWithMsg("insert power error");
		}
		return ResultUtil.success("insert powers success");
	}

	/**
	 * 为角色删除权限
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deletePowerOfRole")
	public Object deletePowerOfRole(@RequestParam(value = "roleId", required = true) int roleId,
			@RequestParam(value = "powerName", required = true) List<String> powerName, HttpServletRequest request) {
		boolean statue = roleService.deletePowerOfRole(roleId, powerName);
		if (statue == false) {
			return ResultUtil.errorWithMsg("delete power error");
		}
		return ResultUtil.success("delete power of role success");
	}

	/**
	 * 角色包含的权限
	 * 
	 * @param roleId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/includePowersOfRole")
	public Object includePowersOfRole(@RequestParam(value = "roleId", required = true) int roleId,
			HttpServletRequest request) {
		List<Power> elements = roleService.includePowers(roleId);
		if (null == elements || elements.size() == 0) {
			return ResultUtil.errorWithMsg("include powers empty");
		}
		return ResultUtil.success(elements);
	}

	/**
	 * 角色包含的权限
	 * 
	 * @param roleId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/notIncludePowersOfRole")
	public Object notIncludePowersOfRole(@RequestParam(value = "roleId", required = true) int roleId,
			HttpServletRequest request) {
		List<Power> elements = roleService.notIncludePowers(roleId);
		if (null == elements || elements.size() == 0) {
			return ResultUtil.errorWithMsg("role have all powers");
		}
		return ResultUtil.success(elements);
	}
}