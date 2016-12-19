package com.hust.mining.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.dao.WebsiteDao;
import com.hust.mining.model.Website;
import com.hust.mining.service.WebsiteService;

@Service
public class WebsiteServiceImpl implements WebsiteService {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteServiceImpl.class);
	@Autowired
	private WebsiteDao websiteDao;

	@Override
	public List<Website> selectAllWebsite() {
		List<Website> website = websiteDao.selecAlltWebsite();
		if (website.isEmpty()) {
			logger.info("website is empty");
			return website;
		}
		return website;
	}

	@Override
	public boolean deleteWebsiteById(long id) {
		int status = websiteDao.deleteWebsiteById(id);
		if (status == 0) {
			logger.info("delete website is error");
			return false;
		}
		return true;
	}

	@Override
	public boolean updateWebsite(Website website) {

		int status = websiteDao.updateWebsite(website);
		if (status == 0) {
			logger.info("update is error");
			return false;
		}
		return true;
	}

	@Override
	public boolean insertWebsite(Website website) {
		if (website.getLevel() == null) {
			logger.info("level is null");
			return false;
		}
		if (website.getName() == null) {
			logger.info("name is null");
			return false;
		}
		if (website.getType() == null) {
			logger.info("type is null");
			return false;
		}
		if (website.getUrl() == null) {
			logger.info("url is null");
			return false;
		}
		int status = websiteDao.insertWebsite(website);
		if (status == 0) {
			logger.info("insert website is error");
			return false;
		}
		return true;
	}

	@Override
	public List<Website> selectByCondition(Website website) {
		List<Website> websites = websiteDao.selectByCondition(website);
		if (websites.isEmpty()) {
			logger.info("website is empty");
			return websites;
		}
		return websites;
	}

}
