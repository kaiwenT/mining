package com.hust.mining.controller;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.service.RedisService;
import com.hust.mining.service.UserService;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "form-username", required = true) String userName,
            @RequestParam(value = "form-password", required = true) String passwd, HttpServletResponse response,
            HttpServletRequest request) {
        // request.getSession().setAttribute(KEY.USER_NAME, userName);
        // return "redirect:page/infoManager.html";
        if (userService.login(userName, passwd)) {
            String sessionid = UUID.randomUUID().toString();
            Cookie session = new Cookie(KEY.SESSION_ID, sessionid);
            Cookie username = new Cookie(KEY.USER_NAME, userName);
            response.addCookie(session);
            response.addCookie(username);
            redisService.setString(KEY.USER_NAME, userName, request);
            return "redirect:page/infoManager.html";
        }
        return "redirect:page/error.jsp";
    }

    @RequestMapping(value = "logout")
    public String logout(HttpServletRequest request) {
        redisService.del(KEY.USER_NAME, request);
        return "redirect:/index.html";
    }
}
