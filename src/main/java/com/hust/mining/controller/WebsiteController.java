package com.hust.mining.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.model.Website;
import com.hust.mining.model.params.WebsiteQueryCondition;
import com.hust.mining.service.WebsiteService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/website")
public class WebsiteController {

	@Autowired
	private WebsiteService websiteService;

	@ResponseBody
	@RequestMapping("/selectAllWebsite")
	public Object selectAllWebsite(@RequestParam(value = "start", required = true) int start,
			@RequestParam(value = "limit", required = true) int limit) {
		List<Website> website = websiteService.selectAllWebsite(start, limit);
		if (website.isEmpty()) {
			return ResultUtil.errorWithMsg("website is empty");
		}
		return ResultUtil.success(website);
	}
	
	@ResponseBody
    @RequestMapping("/selectAllWebsiteUnknow")
    public Object selectAllWebsiteUnknow(@RequestParam(value = "start", required = true) int start,
            @RequestParam(value = "limit", required = true) int limit) {
        List<Website> website = websiteService.selectAllWebsiteUnknow(start, limit);
        if (website.isEmpty()) {
            return ResultUtil.errorWithMsg("website is empty");
        }
        return ResultUtil.success(website);
    }

	@ResponseBody
	@RequestMapping("/insertWebsite")
	public Object insertWebsite(@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "levle", required = true) String level,
			@RequestParam(value = "type", required = true) String type) {
		Website website = new Website();
		website.setUrl(url);
		website.setName(name);
		website.setLevel(level);
		website.setType(type);
		boolean status = websiteService.insertWebsite(website);
		if (status == false) {
			return ResultUtil.errorWithMsg("insert website error");
		}
		return ResultUtil.success("insert website success");
	}

	@ResponseBody
	@RequestMapping("/deleteWebsite")
	public Object deleteWebsiteById(@RequestParam(value = "websiteId", required = true) long websiteId) {
		boolean status = websiteService.deleteWebsiteById(websiteId);
		if (status == false) {
			return ResultUtil.errorWithMsg("delete website error ");
		}
		return ResultUtil.success("delete data success");
	}

	@ResponseBody
	@RequestMapping("/updateWebsite")
	public Object updateWebsite(@RequestParam(value = "id", required = true) long id,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "level", required = true) String level,
			@RequestParam(value = "type", required = true) String type) {
		Website website = new Website();
		website.setId(id);
		website.setUrl(url);
		website.setName(name);
		website.setLevel(level);
		website.setType(type);
		boolean status = websiteService.updateWebsite(website);
		if (status == false) {
			return ResultUtil.errorWithMsg("update website error");
		}
		return ResultUtil.success("update website success");
	}

	@ResponseBody
	@RequestMapping("/selectByCondition")
	public Object selectByCondition(@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "levle", required = true) String level,
			@RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "start", required = true) int start,
			@RequestParam(value = "limit", required = true) int limit) {
		WebsiteQueryCondition website = new WebsiteQueryCondition();
		website.setUrl(url);
		website.setName(name);
		website.setLevel(level);
		website.setType(type);
		website.setStart(start);
		website.setLimit(limit);
		List<Website> websites = websiteService.selectByCondition(website);
		if (websites.isEmpty()) {
			return ResultUtil.errorWithMsg("website is empty");
		}
		return ResultUtil.success(websites);
	}

}
