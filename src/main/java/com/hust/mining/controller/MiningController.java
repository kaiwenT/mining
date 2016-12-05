package com.hust.mining.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.hust.mining.constant.Constant;
import com.hust.mining.constant.Constant.Index;
import com.hust.mining.model.IssueWithBLOBs;
import com.hust.mining.model.params.StatisticParams;
import com.hust.mining.service.ClusterService;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.StatisticService;
import com.hust.mining.util.ConvertUtil;
import com.hust.mining.util.ResultUtil;

@Controller
@RequestMapping("/mining")
public class MiningController {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(MiningController.class);

    @Autowired
    private IssueService issueService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private StatisticService statisticService;

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/cluster")
    public Object cluster(HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("get current issue failed,please create or select a issue");
        }
        IssueWithBLOBs issue = issueService.queryIssueWithBLOBsById(issueId);
        List<String[]> content = null;
        try {
            content = (List<String[]>) ConvertUtil.convertBytesToObject(issue.getFilteredContent());
        } catch (Exception e) {
            return ResultUtil.errorWithMsg("query content from DB failed before cluster \t" + e.toString());
        }
        List<List<String[]>> clusterResult = clusterService.getClusterResult(content, Index.TITLE_INDEX);
        try {
            issue.setClusterResult(ConvertUtil.convertToBytes(clusterResult));
            issue.setModifiedClusterResult(ConvertUtil.convertToBytes(clusterResult));
        } catch (Exception e) {
            logger.error("convert cluster result and origAndCount result to byte[] failed \t" + e.toString());
            return ResultUtil.unknowError();
        }
        if (issueService.updateIssueInfo(issue, request) == 0) {
            return ResultUtil.errorWithMsg("update DB failed after cluster and count");
        }
        return ResultUtil.success("mining complete");
    }

    @ResponseBody
    @RequestMapping("/calOrigAndCountResult")
    public Object calculateOrigAndCountResult(HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("get current issue failed,please create or select a issue");
        }
        List<List<String[]>> list = issueService.queryClusterResult(issueId);
        if (null == list) {
            return ResultUtil.errorWithMsg("query cluster result failed");
        }
        List<String[]> origAndCountResult = statisticService.getOrigAndCount(list, Index.TIME_INDEX);
        IssueWithBLOBs issue = new IssueWithBLOBs();
        issue.setIssueId(issueId);
        try {
            issue.setModifiedOrigCountResult(ConvertUtil.convertToBytes(origAndCountResult));
        } catch (Exception e) {
            logger.error("convert origAndCountResult failed");
            return ResultUtil.errorWithMsg("execute failed");
        }
        if (issueService.updateIssueInfo(issue, request) == 0) {
            return ResultUtil.errorWithMsg("execute failed");
        }
        return ResultUtil.success("execute success");
    }

    @ResponseBody
    @RequestMapping(value = "/statisticSingleSet")
    @SuppressWarnings("unchecked")
    public Object statistic(@RequestBody StatisticParams params, HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("get current issue failed,please create or select a issue");
        }
        IssueWithBLOBs issue = issueService.queryIssueWithBLOBsById(issueId);
        try {
            List<String[]> list =
                    ((List<List<String[]>>) ConvertUtil.convertBytesToObject(issue.getModifiedClusterResult()))
                            .get(params.getCurrentSet());
            Map<String, Map<String, Map<String, Integer>>> timeMap =
                    statisticService.processAll(list, params.getInterval());
            Map<String, Integer> typeMap = statisticService.getTypeCount(timeMap);
            Map<String, Integer> levelMap = statisticService.getLevelCount(timeMap);
            Map<String, Object> map = Maps.newHashMap();
            map.put("time", timeMap);
            Map<String, Object> countMap = Maps.newHashMap();
            countMap.put("type", typeMap);
            countMap.put("level", levelMap);
            map.put("count", countMap);
            return ResultUtil.success(map);
        } catch (Exception e) {
            logger.error("exception occur during statistic\t" + e.toString());
            return ResultUtil.errorWithMsg("统计失败");
        }
    }
}
