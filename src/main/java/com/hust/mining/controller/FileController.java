package com.hust.mining.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.model.Issue;
import com.hust.mining.model.IssueFile;
import com.hust.mining.model.params.Condition;
import com.hust.mining.model.params.IssueQueryCondition;
import com.hust.mining.service.FileService;
import com.hust.mining.service.IssueService;
import com.hust.mining.util.ExcelUtil;
import com.hust.mining.util.ResultUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/file")
public class FileController {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private IssueService issueService;

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Object upload(@RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "titleIndex", required = true) int titleIndex,
            @RequestParam(value = "timeIndex", required = true) int timeIndex,
            @RequestParam(value = "urlIndex", required = true) int urlIndex,
            @RequestParam(value = "sourceType", required = true) String sourceType, HttpServletRequest request) {
        if (request.getSession().getAttribute(KEY.ISSUE_ID) == null) {
            return ResultUtil.errorWithMsg("请选择或者创建一个话题");
        }
        if (file.isEmpty()) {
            return ResultUtil.errorWithMsg("文件为空");
        }
        Condition condition = new Condition();
        condition.setFile(file);
        condition.setTimeIndex(timeIndex);
        condition.setUrlIndex(urlIndex);
        condition.setTitleIndex(titleIndex);
        condition.setSourceType(sourceType);
        if (fileService.insert(condition, request) == 0) {
            return ResultUtil.errorWithMsg("上传失败");
        }
        return ResultUtil.success("上传成功");
    }

    // @SuppressWarnings("unchecked")
    // @RequestMapping("/download")
    // public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Object uuidObj = request.getSession().getAttribute(KEY.ISSUE_ID);
    // String uuid = uuidObj == null ? StringUtils.EMPTY : uuidObj.toString();
    // if (StringUtils.isBlank(uuid)) {
    // response.sendError(404, "未找到当前处理事件，请先创建或者选择某一事件");
    // logger.info("从session中无法获得文件uuid");
    // return;
    // }
    // OutputStream outputStream = null;
    // try {
    // Issue issue = issueService.queryIssueWithBLOBsById(uuid);
    // List<String[]> relist = (List<String[]>) ConvertUtil.convertBytesToObject(issue.getClusterResult());
    // List<String[]> origlist = (List<String[]>) ConvertUtil.convertBytesToObject(issue.getOrigCountResult());
    // outputStream = response.getOutputStream();
    // response.setCharacterEncoding("utf-8");
    // response.setContentType("multipart/form-data");
    // response.setHeader("Content-Disposition", "attachment;fileName=result.xls");
    // HSSFWorkbook workbook = ExcelUtil.exportToExcel(relist, origlist);
    // workbook.write(outputStream);
    // } catch (Exception e) {
    // logger.info("excel 导出失败\t" + e.toString());
    // } finally {
    // try {
    // outputStream.close();
    // } catch (IOException e) {
    // logger.info("导出excel时，关闭outputstream失败");
    // }
    // }
    // }

    @ResponseBody
    @RequestMapping(value = "/queryIssueFiles")
    public Object queryIssueFiles(@RequestParam(value = "issueId", required = true) String issueId,
            HttpServletRequest request) {
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        IssueQueryCondition con = new IssueQueryCondition();
        con.setIssueId(issueId);
        con.setUser(user);
        List<Issue> issues = issueService.queryIssue(con);
        if (issues.isEmpty()) {
            return ResultUtil.errorWithMsg("查询话题文件失败");
        }
        List<IssueFile> list = fileService.queryFilesByIssueId(issueId);
        request.getSession().setAttribute(KEY.ISSUE_ID, issueId);
        JSONObject json = new JSONObject();
        json.put("issue", issues.get(0));
        json.put("list", list);
        return ResultUtil.success(json);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteFileById")
    public Object deleteFileById(@RequestParam(value = "fileid", required = true) String fileId,
            HttpServletRequest request) {
        String issueId = issueService.getCurrentIssueId(request);
        if (StringUtils.isEmpty(issueId)) {
            return ResultUtil.errorWithMsg("获取当前话题失败,请重新进入话题");
        }
        int i = fileService.deleteById(fileId);
        if (i > 0) {
            return ResultUtil.success("删除成功");
        }
        return ResultUtil.errorWithMsg("删除失败");
    }

    @ResponseBody
    @RequestMapping("/getColumnTitle")
    public Object getColumnTime(@RequestParam(value = "file", required = true) MultipartFile file) {
        if (file.isEmpty()) {
            return ResultUtil.errorWithMsg("文件是空的");
        }
        try {
            List<String[]> list = ExcelUtil.read(file.getOriginalFilename(), file.getInputStream(), 0);
            return ResultUtil.success(list.get(0));
        } catch (Exception e) {
            logger.warn("read column title fail" + e.toString());
        }
        return ResultUtil.errorWithMsg("获取列表题失败");
    }
}
