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
import com.hust.mining.model.params.StatisticParams;
import com.hust.mining.service.ResultService;
import com.hust.mining.util.ResultUtil;

@RequestMapping(value = "/result")
public class ResultController {
    @Autowired
    private ResultService resultService;

    @ResponseBody
    @RequestMapping("/getCountResult")
    public Object getCountResult(@RequestParam(value = "resultId", required = true) String resultId,
            HttpServletRequest request) {
        List<String[]> list = resultService.getCountResultById(resultId);
        request.getSession().setAttribute(KEY.RESULT_ID, resultId);
        return ResultUtil.success(list);
    }

    @ResponseBody
    @RequestMapping("/deleteSets")
    public Object delSets(@RequestParam(value = "sets", required = true) int[] sets, HttpServletRequest request) {
        boolean result = resultService.deleteSets(sets, request);
        if (result) {
            return ResultUtil.successWithoutMsg();
        }
        return ResultUtil.unknowError();
    }

    @ResponseBody
    @RequestMapping("/combineSets")
    public Object combineSets(int[] sets, HttpServletRequest request) {
        boolean result = resultService.combineSets(sets, request);
        if (result) {
            return ResultUtil.successWithoutMsg();
        }
        return ResultUtil.unknowError();
    }

    @ResponseBody
    @RequestMapping("/getAllStatResult")
    public Object getAllStatResult(HttpServletRequest request) {
        return null;
    }

    public Object delResultById(@RequestParam(value = "resultId", required = true) String[] resultIds,
            HttpServletRequest request) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/statisticSingleSet")
    public Object statistic(@RequestBody StatisticParams params, HttpServletRequest request) {
        String resultId = resultService.getCurrentResultId(request);
        if (StringUtils.isBlank(resultId)) {
            return ResultUtil.errorWithMsg("get current result failed,please create or select a issue");
        }
        Map<String, Object> map = resultService.statistic(params, request);
        if (map.isEmpty()) {
            return ResultUtil.errorWithMsg("统计失败");
        }
        return ResultUtil.success(map);
    }
}
