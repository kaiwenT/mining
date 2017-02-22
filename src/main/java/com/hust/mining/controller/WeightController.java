package com.hust.mining.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.model.Weight;
import com.hust.mining.service.WeightService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/weight")
public class WeightController {

	@Autowired
	private WeightService weightService;

	@ResponseBody
	@RequestMapping("/selectAllWeight")
	public Object selectAllWeight() {
		List<Weight> weight = weightService.selectAllWeight();
		if (weight.isEmpty()) {
			return ResultUtil.errorWithMsg("weight is empty");
		}
		return ResultUtil.success(weight);
	}

	@ResponseBody
	@RequestMapping("/selectByCondition")
	public Object selectByCondition(@RequestBody Weight weight) {
		List<Weight> weights = weightService.selectByCondition(weight);
		if (weights.isEmpty()) {
			return ResultUtil.errorWithMsg("the condition not exist");
		}
		return ResultUtil.success(weights);
	}

	@ResponseBody
	@RequestMapping("/insertWeight")
	public Object insertWeight(@RequestBody Weight weight) {
		boolean status = weightService.insertWeight(weight);
		if (status == false) {
			return ResultUtil.errorWithMsg("insert is error");
		}
		return ResultUtil.success("insert data success");
	}

	@ResponseBody
	@RequestMapping("/deleteWeight")
	public Object deleteWeight(@RequestParam(value = "weightId", required = true) int weightId) {
		boolean status = weightService.deleteWeightById(weightId);
		if (status == false) {
			return ResultUtil.errorWithMsg("delete is error");
		}
		return ResultUtil.success("delete date success");
	}

	@ResponseBody
	@RequestMapping("/updateWeight")
	public Object updateWeight(@RequestBody Weight weight) {
		boolean status = weightService.updateWeight(weight);
		if (status == false) {
			return ResultUtil.errorWithMsg("update weight error");
		}
		return ResultUtil.success("update date success");
	}
}
