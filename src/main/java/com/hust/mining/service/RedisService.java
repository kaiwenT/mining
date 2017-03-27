package com.hust.mining.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.redis.RedisFacade;

@Service
public class RedisService {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private RedisFacade redis = RedisFacade.getInstance(true);

    public void setObject(String key, Object object, HttpServletRequest request) {
        String sessionid = getSessionid(request);
        if (sessionid == null) {
            return;
        }
        redis.setObject(sessionid + key, object);
        redis.expire(sessionid + key, 600);
    }

    public Object getObject(String key, HttpServletRequest request) {
        String sessionid = getSessionid(request);
        if (sessionid == null) {
            return null;
        }
        return redis.getObject(sessionid + key);
    }

    public void setString(String key, String value, HttpServletRequest request) {
        String sessionid = getSessionid(request);
        if (sessionid == null) {
            return;
        }
        redis.setString(sessionid + key, value);
        redis.expire(sessionid + key, 600);
    }

    public String getString(String key, HttpServletRequest request) {
        String sessionid = getSessionid(request);
        if (sessionid == null) {
            return null;
        }
        String value = redis.getString(sessionid + key);
        return value;
    }

    public void del(String key, HttpServletRequest request) {
        String sessionid = getSessionid(request);
        if (sessionid == null) {
            return;
        }
        redis.delete(sessionid + key);
    }

    private String getSessionid(HttpServletRequest request) {
        String sessionid = request.getSession().getId();
        if (sessionid == null || sessionid == "") {
            logger.error("获取sessionid错误");
        }
        return sessionid;
    }

}
