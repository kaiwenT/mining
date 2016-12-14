package com.hust.mining.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.service.UserService;

@Controller
@RequestMapping("/")
public class AuthController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(@RequestParam(value = "form-username", required = true) String userName,
			@RequestParam(value = "form-password", required = true) String passwd, HttpServletRequest request) {
	    request.getSession().setAttribute(KEY.USER_NAME, userName);
	    return "redirect:page/infoManager.html";
//		if (userService.login(userName, passwd)) {
//			request.getSession().setAttribute("username", userName);
//			return "redirect:page/infoManager.html";
//		}
//		return "redirect:page/error.jsp";
	}

	@RequestMapping(value = "logout")
	public String logout(HttpServletRequest request) {
		userService.logout(request);
		return "redirect:/index.html";
	}
}
