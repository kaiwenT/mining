package com.hust.controller;

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
import com.hust.constants.Constants;
import com.hust.model.Issue;
import com.hust.model.IssueQueryCondition;
import com.hust.model.IssueWithBLOBs;
import com.hust.model.ViewPage;
import com.hust.service.IssueService;
import com.hust.service.UserService;
import com.hust.util.ConvertUtil;
import com.hust.util.ResultUtil;

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

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object createIssue(@RequestParam(value = "issue_name", required = true) String issueName,
            HttpServletRequest request) {
        String user = userService.getCurrentUser(request);
        IssueWithBLOBs issue = new IssueWithBLOBs();
        issue.setIssueId(UUID.randomUUID().toString());
        issue.setIssueName(issueName);
        issue.setCreator(user);
        issue.setCreateTime(new Date());
        issue.setLastOperator(user);
        if (issueService.createIssue(issue) == 0) {
            logger.info("create issue fail");
            return ResultUtil.errorWithMsg("create issue fail");
        }
        request.getSession().setAttribute(Constants.ISSUE_ID, issue.getIssueId());
        return ResultUtil.success("create issue success");
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
            List<List<String[]>> list = (List<List<String[]>>) ConvertUtil
                    .convertBytesToObject(issueService.getIssueById(issueId).getClusterResult());
            ViewPage page = new ViewPage();
            page.setCurrentPage(currentSet);
            page.setTotalPage(list.size());
            resultMap.put("page", page);
            resultMap.put("set", list.get(currentSet - 1));
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
            List<String[]> list = (List<String[]>) ConvertUtil
                    .convertBytesToObject(issueService.getIssueById(issueId).getOrigCountResult());
            return ResultUtil.success(list);
        } catch (Exception e) {
            return ResultUtil.errorWithMsg("从数据库中读取统计结果出错");
        }
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/queryModifiedClusterResult")
    public Object queryModifiedClusterResult(@RequestParam(value = "currentset", required = true) int currentSet,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            List<List<String[]>> list = (List<List<String[]>>) ConvertUtil
                    .convertBytesToObject(issueService.getIssueById(issueId).getModifiedClusterResult());
            ViewPage page = new ViewPage();
            page.setCurrentPage(currentSet);
            page.setTotalPage(list.size());
            resultMap.put("page", page);
            resultMap.put("set", list.get(currentSet - 1));
        } catch (Exception e) {
            return ResultUtil.errorWithMsg("从数据库中读取聚类结果出错");
        }
        return ResultUtil.success(resultMap);
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/queryModifiedOrigAndCountResult")
    public Object queryModifiedOrigAndCountResult(HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        try {
            List<String[]> list = (List<String[]>) ConvertUtil
                    .convertBytesToObject(issueService.getIssueById(issueId).getModifiedOrigCountResult());
            return ResultUtil.success(list);
        } catch (Exception e) {
            return ResultUtil.errorWithMsg("从数据库中读取统计结果出错");
        }
    }

    @ResponseBody
    @RequestMapping("/deleteItemsFromModifiedClusterResult")
    public Object deleteItemsFromModifiedClusterResult(
            @RequestParam(value = "currentset", required = true) int currentset,
            @RequestParam(value = "indexes", required = true) int[] indexes, HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        boolean result = issueService.deleteItemsFromModifiedClusterResult(currentset, indexes, request);
        if (result) {
            return ResultUtil.success("删除成功");
        } else {
            return ResultUtil.success("删除失败");
        }
    }

    @ResponseBody
    @RequestMapping("/deleteItemsFromOrigClusterResult")
    public Object deleteItemsFromOrigClusterResult(@RequestParam(value = "currentset", required = true) int currentset,
            @RequestParam(value = "indexes", required = true) int[] indexes, HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        boolean result = issueService.deleteItemsFromOrigClusterResult(currentset, indexes, request);
        if (result) {
            return ResultUtil.success("删除成功");
        } else {
            return ResultUtil.success("删除失败");
        }
    }

    @ResponseBody
    @RequestMapping("/combineModifiedResult")
    public Object combineModifiedResult(@RequestParam(value = "indexes", required = true) int[] indexes,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }
        boolean result = issueService.combineModifiedCountResult(indexes, request);
        if (result) {
            return ResultUtil.success("删除成功");
        } else {
            return ResultUtil.success("删除失败");
        }
    }

    @ResponseBody
    @RequestMapping("/combineResult")
    public Object combineResult(@RequestParam(value = "indexes", required = true) int[] indexes,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isBlank(issueId)) {
            return ResultUtil.errorWithMsg("无法获取issueid，请重新选择或者创建issue");
        }

        return ResultUtil.success("合并成功");
    }

    @ResponseBody
    @RequestMapping("/queryOwnIssue")
    public Object queryOwnIssue(@RequestBody IssueQueryCondition con, HttpServletRequest request) {
        String user = userService.getCurrentUser(request);
        con.setUser(user);
        List<Issue> list = issueService.queryIssue(con);
        return ResultUtil.success(list);
    }

    @ResponseBody
    @RequestMapping("/queryAllIssue")
    public Object queryAllIssue(@RequestBody IssueQueryCondition con, HttpServletRequest request) {
        List<Issue> list = issueService.queryIssue(con);
        return ResultUtil.success(list);
    }
}
