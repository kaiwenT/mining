package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.Website;

public interface WebsiteService {

	List<Website> selectAllWebsite();

	List<Website> selectByCondition(Website website);

	boolean deleteWebsiteById(long id);

	boolean updateWebsite(Website website);

	boolean insertWebsite(Website website);
}
