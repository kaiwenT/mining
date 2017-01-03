package com.hust.mining.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.mining.dao.mapper.WebsiteMapper;
import com.hust.mining.model.Website;
import com.hust.mining.model.WebsiteExample;
import com.hust.mining.model.WebsiteExample.Criteria;
@Repository
public class WebsiteDao {

	@Autowired
	private WebsiteMapper websiteMapper;

	public String queryLevelByUrl(String url) {
		WebsiteExample example = new WebsiteExample();
		example.createCriteria().andUrlEqualTo(url);
		return websiteMapper.selectByExample(example).get(0).getLevel();
	}

	public String queryTypeByUrl(String url) {
		WebsiteExample example = new WebsiteExample();
		example.createCriteria().andUrlEqualTo(url);
		return websiteMapper.selectByExample(example).get(0).getType();
	}

	public Website queryByUrl(String url) {
		WebsiteExample example = new WebsiteExample();
		example.createCriteria().andUrlEqualTo(url);
		List<Website> list = websiteMapper.selectByExample(example);
		if (null == list || list.size() == 0) {
			Website web = new Website();
			web.setLevel("其他");
			web.setName("其他");
			web.setType("其他");
			web.setUrl(url);
			return web;
		}
		return list.get(0);
	}

	public List<Website> selecAlltWebsite() {
		WebsiteExample example = new WebsiteExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIsNotNull();
		List<Website> website = websiteMapper.selectByExample(example);
		return website;
	}

	public int insertWebsite(Website werbsite) {
		return websiteMapper.insert(werbsite);
	}

	public List<Website> selectByCondition(Website webSite) {
		WebsiteExample example = new WebsiteExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(webSite.getUrl())) {
			criteria.andUrlLike(webSite.getUrl());
		}
		if (!StringUtils.isBlank(webSite.getLevel())) {
			criteria.andLevelLike(webSite.getLevel());
		}
		if (!StringUtils.isBlank(webSite.getName())) {
			criteria.andNameLike(webSite.getName());
		}
		if (!StringUtils.isBlank(webSite.getType())) {
			criteria.andTypeLike(webSite.getType());
		}
		List<Website> websites = websiteMapper.selectByExample(example);
		return websites;
	}

	public int deleteWebsiteById(long id) {
		WebsiteExample example = new WebsiteExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		return websiteMapper.deleteByExample(example);
	}

	public int updateWebsite(Website website) {
		return websiteMapper.updateByPrimaryKeySelective(website);
	}
}
