package com.hust.mining.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.model.Issue;
import com.hust.mining.model.params.IssueQueryCondition;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.RedisService;
import com.hust.mining.service.UserService;
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
    private RedisService redisService;

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object createIssue(@RequestParam(value = "issueName", required = true) String issueName,
            HttpServletRequest request) {
        if (issueService.createIssue(issueName, request) == 0) {
            logger.info("create issue fail");
            return ResultUtil.errorWithMsg("创建任务失败");
        }
        return ResultUtil.success("创建任务成功");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteIssue(@RequestParam(value = "issueId", required = true) String issueId,
            HttpServletRequest request) {
        if (issueService.deleteIssueById(issueId, request) > 0) {
            return ResultUtil.success("删除任务成功");
        }
        return ResultUtil.errorWithMsg("删除任务失败");
    }

    @ResponseBody
    @RequestMapping("/queryOwnIssue")
    public Object queryOwnIssue(@RequestBody IssueQueryCondition con, HttpServletRequest request) {
        String user = userService.getCurrentUser(request);
        con.setUser(user);
        List<Issue> list = issueService.queryIssue(con);
        long count = list.size();
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
        long count = list.size();
        JSONObject result = new JSONObject();
        long pageTotal = count % 10 == 0 ? (count / 10) : (count / 10 + 1);
        result.put("pageTotal", pageTotal);
        result.put("list", list);
        return ResultUtil.success(result);
    }

    @ResponseBody
    @RequestMapping("/miningByTime")
    public Object miningByTime(@RequestParam(value = "startTime", required = true) Date startTime,
            @RequestParam(value = "endTime", required = true) Date endTime, HttpServletRequest request) {
        String issueId = redisService.getString(KEY.ISSUE_ID, request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("请重新选择任务");
        }
        List<String[]> count = issueService.miningByTime(startTime, endTime, request);
        if (count == null) {
            return ResultUtil.unknowError();
        }
        return ResultUtil.success(count);
    }

    @ResponseBody
    @RequestMapping("/miningByFile")
    public Object miningByFileIds(@RequestBody List<String> fileIds, HttpServletRequest request) {
        String issueId = redisService.getString(KEY.ISSUE_ID, request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("请重新选择任务");
        }
        List<String[]> count = issueService.miningByFileIds(fileIds, request);
        if (count == null) {
            return ResultUtil.unknowError();
        }
        return ResultUtil.success(count);
    }

    @ResponseBody
    @RequestMapping("/miningSingleFile")
    public Object miningSingleFile(@RequestParam(value = "fileId", required = true) String fileId,
            HttpServletRequest request) {
        String issueId = redisService.getString(KEY.ISSUE_ID, request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("请重新选择任务");
        }
        List<String> list = new ArrayList<String>();
        list.add(fileId);
        List<String[]> count = issueService.miningByFileIds(list, request);
        if (count == null) {
            return ResultUtil.unknowError();
        }
        return ResultUtil.successWithoutMsg();
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        CustomDateEditor editor = new CustomDateEditor(df, false);
        binder.registerCustomEditor(Date.class, editor);
    }

}
