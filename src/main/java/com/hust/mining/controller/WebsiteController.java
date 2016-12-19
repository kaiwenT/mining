package com.hust.mining.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.model.Website;
import com.hust.mining.service.WebsiteService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/website")
public class WebsiteController {

	@Autowired
	private WebsiteService websiteService;

	@ResponseBody
	@RequestMapping("/selectAllWebsite")
	public Object selectAllWebsite() {
		List<Website> website = websiteService.selectAllWebsite();
		if (website.isEmpty()) {
			return ResultUtil.errorWithMsg("website is empty");
		}
		return ResultUtil.success(website);
	}

	@ResponseBody
	@RequestMapping("/insertWebsite")
	public Object insertWebsite(@RequestBody Website website) {
		boolean status = websiteService.insertWebsite(website);
		if (status == false) {
			return ResultUtil.errorWithMsg("insert error");
		}
		return ResultUtil.success("insert data success");
	}

	@ResponseBody
	@RequestMapping("/deleteWebsite")
	public Object deleteWebsiteById(@RequestParam(value = "id", required = true) long id) {
		boolean status = websiteService.deleteWebsiteById(id);
		if (status == false) {
			return ResultUtil.errorWithMsg("delete website error ");
		}
		return ResultUtil.success("delete data success");
	}

	@ResponseBody
	@RequestMapping("/updateWebsite")
	public Object updateWebsite(@RequestBody Website website) {
		boolean status = websiteService.updateWebsite(website);
		if (status == false) {
			return ResultUtil.errorWithMsg("update is error");
		}
		return ResultUtil.success("update data success");
	}

	@ResponseBody
	@RequestMapping("/selectByCondition")
	public Object selectByCondition(@RequestBody Website website) {
		List<Website> websites = websiteService.selectByCondition(website);
		if (websites.isEmpty()) {
			return ResultUtil.errorWithMsg("website is empty");
		}
		return ResultUtil.success(websites);
	}

}
