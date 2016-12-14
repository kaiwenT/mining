package com.hust.mining.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hust.mining.service.UserService;
import com.hust.mining.util.ResultUtil;

public class PermissionInterceptor implements HandlerInterceptor {
	/**
	 * Logger for this class
	 */
	@Autowired
	private UserService userService;
	private static final Logger LOG = LoggerFactory.getLogger(PermissionInterceptor.class);

	@Autowired
	private MappingJackson2HttpMessageConverter converter;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			String url = request.getRequestURI();
			String requestPath = request.getServletPath();
			// 登陆成功以后 当数据库权限是一定的 但是你添加权限信息，程序缓存的还是以前的权限信息 ，更新的权限没有添加到里面
			// 现在解决办法就是 在拦截器里面 可以重新获取权限信息
			// 先判断
			if ("/".equals(url) || "/index.html".equals(url)) {
				if (null != request.getSession().getAttribute("username")) {
					response.sendRedirect("/page/infoManager.html");
				} else {
					return true;
				}
			} else {
				// 请求页面的时候会判断 用户是否登录了，假如登陆了就再去判断权限，否则直接跳转到登录界面
				if (request.getSession().getAttribute("username") != null) {
					if (request.getSession().getAttribute("userPowerUrl") != null) {
						request.getSession().removeAttribute("userPowerUrl");
					}
					// 在这里可以重新获取
					List<String> userPowerUrl = userService
							.selectUserPowerUrl(request.getSession().getAttribute("username").toString());
					System.out.println(
							"用户权限" + request.getSession().getAttribute("username").toString() + userPowerUrl.size());
					request.getSession().setAttribute("userPowerUrl", userPowerUrl);
					if (userPowerUrl.contains(requestPath)) {
						return true;
					} else {
						LOG.warn("PermissionDeny: errorMsg=用户{}没有权限，访问的URL：{}", request.getRemoteHost(),
								request.getRequestURI());
						fail(response);
					}
				} else {
					LOG.warn("PermissionDeny: errorMsg=用户{}没有权限，访问的URL：{}", request.getRemoteHost(),
							request.getRequestURI());
					fail(response);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("permissionInterceptor error \t" + e.toString());
			try {
				response.sendRedirect("/page/error.html");
			} catch (Exception e1) {
				LOG.error("跳转发生异常" + e1.toString());
			}
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

	private void fail(HttpServletResponse response) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			converter.write(ResultUtil.errorWithMsg("非常抱歉，您没有权限访问该资源，请联系管理员"), MediaType.APPLICATION_JSON,
					new ServletServerHttpResponse(response));
		} catch (Throwable e) {
			LOG.error("ajax error \t" + e);
		}
	}

}
