package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.dao.PowerDao;
import com.hust.mining.dao.RoleDao;
import com.hust.mining.dao.RolePowerDao;
import com.hust.mining.dao.UserRoleDao;
import com.hust.mining.model.Power;
import com.hust.mining.model.Role;
import com.hust.mining.model.RolePower;
import com.hust.mining.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private PowerDao powerDao;
	@Autowired
	private RolePowerDao rolePowerDao;
	@Autowired
	private UserRoleDao userRoleDao;

	@Override
	public List<Role> selectAllRole() {
		List<Role> roles = roleDao.selectRoles();
		if (roles.isEmpty()) {
			logger.info("role  is empty");
		}
		return roles;
	}

	@Override
	public List<Role> selectOneRoleInfo(String roleName) {
		// 角色名必须是表已经存在的角色名
		List<Role> role = roleDao.selectByLikeRoleName(roleName);
		if (role.isEmpty()) {
			logger.info("roleName is not exist");
		}
		return role;
	}

	@Override
	public boolean insertRoleInfo(String roleName) {
		// 添加新的角色信息，信息不能重复
		List<Role> roles = roleDao.selectRoleByName(roleName);
		if (!roles.isEmpty()) {
			logger.info("role table have been roelinfo");
			return false;
		}
		// 状态值
		int statue = roleDao.insertRole(roleName);
		if (statue == 0) {
			logger.info("insert role error");
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteRoleInfoById(int roleId) {
		int statue = userRoleDao.deleteUserRoleByRoleId(roleId);
		if (statue == 0) {
			logger.info("delete userRole is error");
			return false;
		}
		int rolePowerStatus = rolePowerDao.deleteRolePowerByRoleId(roleId);
		if (rolePowerStatus == 0) {
			logger.info("delete rolepower about roleidinfo is error");
			return false;
		}
		int roleStatus = roleDao.deleteByPrimaryKey(roleId);
		if (roleStatus == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean updateRoleInfo(Role role) {
		// 更新角色信息也不能更新成数据库中已经存在的信息
		int statue = roleDao.updateByPrimaryKeySelective(role);
		if (statue == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 为角色添加权限 是从角色不具有的权限中添加
	 */
	@Override
	public boolean insertPowerOfRole(int roleId, List<String> powerName) {

		List<Integer> powerId = new ArrayList<>();
		for (String powerNameIn : powerName) {
			List<Power> power = powerDao.selectPowerByPowerName(powerNameIn);
			powerId.add(power.get(0).getPowerId());
		}
		for (int powerIdInfo : powerId) {
			RolePower rolePower = new RolePower();
			rolePower.setPowerId(powerIdInfo);
			rolePower.setRoleId(roleId);
			int statue = rolePowerDao.insert(rolePower);
			if (statue == 0) {
				logger.info("insert power error");
				return false;
			}
		}
		return true;
	}

	/**
	 * 从角色中删除某些权限， 是根据角色中包含的权限进行删除
	 */
	@Override
	public boolean deletePowerOfRole(int roleId, List<String> powerName) {
		List<Integer> powerIds = new ArrayList<>();
		for (String powerNameIn : powerName) {
			List<Power> power = powerDao.selectPowerByPowerName(powerNameIn);
			powerIds.add(power.get(0).getPowerId());
		}
		for (int powerId : powerIds) {
			int statue = rolePowerDao.deleteRolePowerById(powerId, roleId);
			if (statue == 0) {
				logger.info("delete power error");
				return false;
			}
		}
		return true;
	}

	/**
	 * 为用户添加角色时，要先查询用户不具有的角色新
	 */
	@Override
	public List<Role> selectNotHaveRole(int roleId) {
		List<Role> roles = roleDao.selectRoles();
		List<Role> role = new ArrayList<>();
		for (Role roleInfo : roles) {
			if (roleInfo.getRoleId() != roleId) {
				role.add(roleInfo);
			}
		}
		return role;
	}

	/**
	 * 角色不包含的权限信息
	 */
	@Override
	public List<Power> notIncludePowers(int roleId) {
		// 在角色权限表中 把权限ID取出来 。然后再得到权限名字
		List<Integer> powerIds = new ArrayList<>();
		List<RolePower> rolePowers = rolePowerDao.selectRolePowerByRoleId(roleId);
		for (RolePower rolePowerInfo : rolePowers) {
			powerIds.add(rolePowerInfo.getPowerId());
		}
		List<Power> notIncludePower = new ArrayList<>();
		List<Power> powers = powerDao.selectAllPowers();
		for (Power powerInfo : powers) {
			for (int powerId : powerIds) {
				if (powerInfo.getPowerId() != powerId) {
					notIncludePower.add(powerInfo);
				}
			}
		}
		return notIncludePower;
	}

	/**
	 * 角色包含的权限
	 */
	@Override
	public List<Power> includePowers(int roleId) {
		List<Integer> powerIds = new ArrayList<>();
		List<RolePower> rolePowers = rolePowerDao.selectRolePowerByRoleId(roleId);
		for (RolePower rolePowerInfo : rolePowers) {
			powerIds.add(rolePowerInfo.getPowerId());
		}
		List<Power> includePower = new ArrayList<>();
		List<Power> powers = powerDao.selectAllPowers();
		for (Power powerInfo : powers) {
			for (int powerId : powerIds) {
				if (powerInfo.getPowerId() == powerId) {
					includePower.add(powerInfo);
				}
			}
		}
		return includePower;
	}
}