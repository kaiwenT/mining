package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.Website;
import com.hust.mining.model.params.WebsiteQueryCondition;

public interface WebsiteService {

	List<Website> selectAllWebsite();

	List<Website> selectByCondition(WebsiteQueryCondition  website);

	boolean deleteWebsiteById(long id);

	boolean updateWebsite(Website website);

	boolean insertWebsite(Website website);
}
