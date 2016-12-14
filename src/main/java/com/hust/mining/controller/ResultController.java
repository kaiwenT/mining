package com.hust.mining.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.model.Result;
import com.hust.mining.model.params.StatisticParams;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.ResultService;
import com.hust.mining.util.ResultUtil;

@RequestMapping(value = "/result")
public class ResultController {
    @Autowired
    private ResultService resultService;
    @Autowired
    private IssueService issueService;

    @ResponseBody
    @RequestMapping("/getCountResult")
    public Object getCountResult(@RequestParam(value = "resultId", required = true) String resultId,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("请重新选择话题");
        }
        List<String[]> list = resultService.getCountResultById(resultId, issueId);
        if (null == list || list.size() == 0) {
            return ResultUtil.errorWithMsg("不存在记录");
        }
        request.getSession().setAttribute(KEY.RESULT_ID, resultId);
        return ResultUtil.success(list);
    }

    @ResponseBody
    @RequestMapping("/deleteSets")
    public Object delSets(@RequestParam(value = "sets", required = true) int[] sets, HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("请重新选择话题");
        }
        String resultId = resultService.getCurrentResultId(request);
        if (StringUtils.isEmpty(resultId)) {
            return ResultUtil.errorWithMsg("请重新选择一条挖掘记录");
        }
        boolean result = resultService.deleteSets(sets, request);
        if (result) {
            return ResultUtil.success("删除成功");
        }
        return ResultUtil.errorWithMsg("删除失败");
    }

    @ResponseBody
    @RequestMapping("/combineSets")
    public Object combineSets(int[] sets, HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("请重新选择话题");
        }
        String resultId = resultService.getCurrentResultId(request);
        if (StringUtils.isEmpty(resultId)) {
            return ResultUtil.errorWithMsg("请重新选择一条挖掘记录");
        }
        boolean result = resultService.combineSets(sets, request);
        if (result) {
            return ResultUtil.success("合并成功");
        }
        return ResultUtil.errorWithMsg("合并失败");
    }

    @ResponseBody
    @RequestMapping("/queryResultList")
    public Object queryResultList(HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("获取当前话题失败,请重新进入话题");
        }
        List<Result> list = resultService.queryResultsByIssueId(issueId);
        if (null == list || list.size() == 0) {
            return ResultUtil.errorWithMsg("查询失败");
        }
        return ResultUtil.success(list);
    }

    @ResponseBody
    @RequestMapping("/delResultById")
    public Object delResultById(@RequestParam(value = "resultId", required = true) String resultId,
            HttpServletRequest request) {
        int del = resultService.delResultById(resultId);
        if (del <= 0) {
            return ResultUtil.errorWithMsg("删除失败");
        }
        return ResultUtil.success("删除成功");
    }

    @ResponseBody
    @RequestMapping(value = "/statisticSingleSet")
    public Object statistic(@RequestBody StatisticParams params, HttpServletRequest request) {
        String resultId = resultService.getCurrentResultId(request);
        if (StringUtils.isBlank(resultId)) {
            return ResultUtil.errorWithMsg("请重新选择话题");
        }
        Map<String, Object> map = resultService.statistic(params, request);
        if (null == map || map.isEmpty()) {
            return ResultUtil.errorWithMsg("统计失败");
        }
        return ResultUtil.success(map);
    }
}
