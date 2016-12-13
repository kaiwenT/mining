package com.hust.mining.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.hust.mining.constant.Constant;
import com.hust.mining.constant.Constant.Interval;
import com.hust.mining.model.Issue;
import com.hust.mining.model.IssueWithBLOBs;
import com.hust.mining.model.params.DeleteItemsParams;
import com.hust.mining.model.params.IssueQueryCondition;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.StatisticService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.ConvertUtil;
import com.hust.mining.util.ResultUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/issue")
public class IssueController {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(IssueController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private StatisticService statisticService;

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object createIssue(@RequestParam(value = "issueName", required = true) String issueName,
            HttpServletRequest request) {
        String user = userService.getCurrentUser(request);
        IssueWithBLOBs issue = new IssueWithBLOBs();
        issue.setIssueId(UUID.randomUUID().toString());
        issue.setIssueName(issueName);
        issue.setCreator(user);
        issue.setCreateTime(new Date());
        issue.setLastOperator(user);
        issue.setLastUpdateTime(issue.getLastUpdateTime());
        if (issueService.createIssue(issue) == 0) {
            logger.info("create issue fail");
            return ResultUtil.errorWithMsg("create issue fail");
        }
        request.getSession().setAttribute(Constant.ISSUE_ID, issue.getIssueId());
        return ResultUtil.success("create issue success");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteIssue(@RequestParam(value = "issueId", required = true) String issueId) {
        if (issueService.deleteIssueById(issueId) > 0) {
            return ResultUtil.success("delete success");
        }
        return ResultUtil.errorWithMsg("delete failed");
    }

    @ResponseBody
    @RequestMapping(value = "/shuffle", method = RequestMethod.POST)
    public Object shuffle(HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("query current issue failed,please create or select a issue");
        }
        String user = userService.getCurrentUser(request);
        if (issueService.combineFiles(issueId, user) == 0) {
            return ResultUtil.errorWithMsg("combine different files failed");
        }
        return ResultUtil.success("combine success");
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/queryClusterResult")
    public Object queryClusterResult(@RequestParam(value = "currentset", required = true) int currentSet,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            List<List<String[]>> allList = (List<List<String[]>>) ConvertUtil
                    .convertBytesToObject(issueService.queryIssueWithBLOBsById(issueId).getModifiedClusterResult());
            List<String[]> list = allList.get(currentSet);
            resultMap.put("set", list);
            Map<String, Map<String, Map<String, Integer>>> timeMap = statisticService.processAll(list, Interval.DAY);
            resultMap.put("statis", timeMap);
        } catch (Exception e) {
            return ResultUtil.errorWithMsg("从数据库中读取聚类结果出错");
        }
        return ResultUtil.success(resultMap);
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/queryOrigAndCountResult")
    public Object queryOrigAndCountResult(HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        try {
            IssueQueryCondition con = new IssueQueryCondition();
            con.setIssueId(issueId);
            List<Issue> issues = issueService.queryIssue(con);
            if (issues.isEmpty()) {
                return ResultUtil.errorWithMsg("query issue info failed");
            }
            List<String[]> list = (List<String[]>) ConvertUtil
                    .convertBytesToObject(issueService.queryIssueWithBLOBsById(issueId).getModifiedOrigCountResult());
            JSONObject json = new JSONObject();
            json.put("issue", issues.get(0));
            json.put("list", list);
            return ResultUtil.success(json);
        } catch (Exception e) {
            return ResultUtil.errorWithMsg("从数据库中读取统计结果出错");
        }
    }

    @ResponseBody
    @RequestMapping("/deleteItemsFromClusterResult")
    public Object deleteItemsFromClusterResult(@RequestParam(value = "currentset", required = true) int currentset,
            @RequestParam(value = "indexset", required = true) int[] indexset, HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        DeleteItemsParams params = new DeleteItemsParams();
        params.setCurrentSet(currentset);
        params.setIndexSet(indexset);
        boolean result = issueService.deleteItemsFromClusterResult(params, request);
        if (result) {
            return ResultUtil.success("删除成功");
        } else {
            return ResultUtil.success("删除失败");
        }
    }

    @ResponseBody
    @RequestMapping("/deleteSetsFromClusterResult")
    public Object deleteSetsFromClusterResult(@RequestParam(value = "indexSet", required = true) int[] indexs,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        boolean result = issueService.deleteSetsFromClusterResult(indexs, request);
        if (result) {
            return ResultUtil.success("删除成功");
        } else {
            return ResultUtil.success("删除失败");
        }
    }

    @ResponseBody
    @RequestMapping("/combineResult")
    public Object combineResult(@RequestParam(value = "indexSet", required = true) int[] indexes,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        boolean result = issueService.combineCountResult(indexes, request);
        if (result) {
            return ResultUtil.success("合并成功");
        } else {
            return ResultUtil.success("合并失败");
        }
    }

    @ResponseBody
    @RequestMapping("/queryOwnIssue")
    public Object queryOwnIssue(@RequestBody IssueQueryCondition con, HttpServletRequest request) {
        String user = userService.getCurrentUser(request);
        con.setUser(user);
        long count = issueService.countIssues(con);
        List<Issue> list = issueService.queryIssue(con);
        JSONObject result = new JSONObject();
        long pageTotal = count % 10 == 0 ? (count / 10) : (count / 10 + 1);
        result.put("pageTotal", pageTotal);
        result.put("list", list);
        return ResultUtil.success(result);
    }

    @ResponseBody
    @RequestMapping("/queryAllIssue")
    public Object queryAllIssue(@RequestBody IssueQueryCondition con, HttpServletRequest request) {
        List<Issue> list = issueService.queryIssue(con);
        return ResultUtil.success(list);
    }

    @ResponseBody
    @RequestMapping("/reset")
    public Object reset(HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        boolean reset = issueService.reset(request);
        if (reset) {
            return ResultUtil.success("reset success");
        }
        return ResultUtil.errorWithMsg("reset failed");

    }
}
