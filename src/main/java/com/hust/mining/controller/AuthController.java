package com.hust.mining.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.service.RedisService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "form-username", required = true) String userName,
            @RequestParam(value = "form-password", required = true) String passwd, HttpServletResponse response,
            HttpServletRequest request) {
        // request.getSession().setAttribute(KEY.USER_NAME, userName);
        // return "redirect:page/infoManager.html";
        if (userService.login(userName, passwd)) {
            Cookie username = new Cookie(KEY.USER_NAME, userName);
            username.setPath("/");
            response.addCookie(username);
            redisService.setString(KEY.USER_NAME, userName, request);
            return "redirect:/topic_list.html";
        }
        return "redirect:/error.jsp";
    }

    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Object logout(HttpServletRequest request) {
        redisService.del(KEY.USER_NAME, request);
        return ResultUtil.successWithoutMsg();
    }

    @ResponseBody
    @RequestMapping(value = "/getCurrentUser", method = RequestMethod.POST)
    public Object getCurrentUser(HttpServletRequest request) {
        String user = userService.getCurrentUser(request);
        return ResultUtil.success(user);
    }
}
