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
import com.hust.mining.service.PowerService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/power")
public class PowerController {
	@Autowired
	private PowerService powerService;

	@ResponseBody
	@RequestMapping("/selectAllPower")
	public Object selectAllPower(HttpServletRequest request) {
		List<Power> powers = powerService.selectAllPower();
		if (null == powers || powers.size() == 0) {
			return ResultUtil.errorWithMsg("empty is empty");
		}
		return ResultUtil.success(powers);
	}

	/**
	 * 查询单条角色信息
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectOnePowerInfo")
	public Object selectOnePowerInfo(@RequestParam(value = "powerName", required = true) String powerName,
			HttpServletRequest request) {
		List<Power> element = powerService.selectOnePowerInfo(powerName);
		if (null == element || element.size() == 0) {
			return ResultUtil.errorWithMsg("power table not have this powerName");
		}
		return ResultUtil.success(element);
	}

	/**
	 * 添加权限信息
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insertPowerInfo")
	public Object insertPowerInfo(@RequestBody Power power, HttpServletRequest request) {
		boolean statue = powerService.insertPowerInfo(power);
		if (statue == false) {
			return ResultUtil.errorWithMsg("power table have this power");
		}
		return ResultUtil.success("insert data success");
	}

	/**
	 * 删除权限信息：根据权限ID
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deletePowerById")
	public Object deletePowerById(@RequestParam(value = "powerId", required = true) int powerId,
			HttpServletRequest request) {
		boolean statue = powerService.deletePowerById(powerId);
		if (statue == false) {
			return ResultUtil.errorWithMsg("power/rolepower table not have this power");
		}
		return ResultUtil.success("delete data success");
	}

	/**
	 * 更新权限信息
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePowerInfo")
	public Object updatePowerInfo(@RequestBody Power power, HttpServletRequest request) {
		boolean statue = powerService.updatePowerInfo(power);
		if (statue == false) {
			return ResultUtil.unknowError();
		}
		return ResultUtil.success("update data success");
	}
}
