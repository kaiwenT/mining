package com.hust.mining.dao;

import java.util.List;

import com.hust.mining.dao.mapper.WebsiteMapper;
import com.hust.mining.model.Website;
import com.hust.mining.model.WebsiteExample;

public class WebsiteDao {

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
            return null;
        }
        return list.get(0);
    }
}
