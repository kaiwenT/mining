package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.Power;
import com.hust.mining.model.Role;


public interface RoleService {
	List<Role> selectAllRole();

	List<Role> selectOneRoleInfo(String roleName);

	boolean insertRoleInfo(String roleName);

	boolean deleteRoleInfoById(int roleId);

	boolean updateRoleInfo(Role role);

	boolean insertPowerOfRole(int roleId, List<String> powerName);

	boolean deletePowerOfRole(int roleId, List<String> powerName);

	List<Role> selectNotHaveRole(int roleId);

	List<Power> notIncludePowers(int roleId);

	List<Power> includePowers(int roleId);

}
