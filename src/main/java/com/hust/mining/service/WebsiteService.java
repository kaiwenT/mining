package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.Website;
import com.hust.mining.model.params.WebsiteQueryCondition;

public interface WebsiteService {

	List<Website> selectAllWebsite(int start, int limit);

	List<Website> selectByCondition(WebsiteQueryCondition website);
	
	List<Website> selectAllWebsiteUnknow(int start, int limit);

	boolean deleteWebsiteById(long id);

	boolean updateWebsite(Website website);

	boolean insertWebsite(Website website);
}
