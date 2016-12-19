package com.hust.mining.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.model.SourceType;
import com.hust.mining.service.SourceTypeService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/sourceType")
public class SourceTypeContrller {

	@Autowired
	private SourceTypeService sourceTypeService;

	@ResponseBody
	@RequestMapping(value = "/selectAllSourceType")
	public Object selectAllSourceType() {
		List<SourceType> sourceType = sourceTypeService.selectSourceType();
		if (sourceType.isEmpty()) {
			return ResultUtil.errorWithMsg("sourceType is empty");
		}
		return ResultUtil.success(sourceType);
	}

	@ResponseBody
	@RequestMapping(value = "/selectSourceTypeByName")
	public Object selectSourceTypeByName(@RequestParam(value = "name", required = true) String name) {
		List<SourceType> sourceType = sourceTypeService.selectSourceTypeByName(name);
		if (sourceType.isEmpty()) {
			return ResultUtil.errorWithMsg("The name is not exist");
		}
		return ResultUtil.success(sourceType);
	}

	@ResponseBody
	@RequestMapping(value = "/deleteSourceTypeById")
	public Object deleteSourceTypeById(@RequestParam(value = "id", required = true) int id) {
		int status = sourceTypeService.deleteSourceTypeById(id);
		if (status == 0) {
			return ResultUtil.errorWithMsg("delete sourcetype is error");
		}
		return ResultUtil.success("delete data success");
	}

	@ResponseBody
	@RequestMapping(value = "/insertSourceType")
	public Object insertSourceType(@RequestParam(value = "name", required = true) String name) {
		int status = sourceTypeService.insertSourceType(name);
		if (status == 0) {
			return ResultUtil.errorWithMsg("insert is error");
		}
		return ResultUtil.success("insert data success");
	}

	@ResponseBody
	@RequestMapping(value = "/updateSourceType")
	public Object updateSourceType(@RequestBody SourceType sourceType) {
		int status = sourceTypeService.updateSourceType(sourceType);
		if (status == 0) {
			return ResultUtil.errorWithMsg("update data error");
		}
		return ResultUtil.success("update data success");
	}

}
