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

import com.hust.mining.util.ResultUtil;

public class PermissionInterceptor implements HandlerInterceptor {
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PermissionInterceptor.class);

	@Autowired
	private MappingJackson2HttpMessageConverter converter;

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			String url = request.getRequestURI();
			String requestPath = request.getServletPath();
			List<String> powers = (List<String>) request.getSession().getAttribute("userPowerUrl");
			if ("/".equals(url) || "/index.html".equals(url)) {
				if (null != request.getSession().getAttribute("username")) {
					response.sendRedirect("/page/main.html");
				} else {
					return true;
				}
			} else {
				if (null != request.getSession().getAttribute("username")) {
					if (powers.contains(requestPath)) {
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
		} catch (

		Exception e) {
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
