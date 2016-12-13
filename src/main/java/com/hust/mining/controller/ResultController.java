package com.hust.mining.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.mining.service.ResultService;
import com.hust.mining.util.ResultUtil;

@RequestMapping(value = "/result")
public class ResultController {
    @Autowired
    private ResultService resultService;

    @ResponseBody
    @RequestMapping("/getCountResult")
    public Object getCountResult(HttpServletRequest request) {
        String resultId = resultService.getCurrentResultId(request);
        List<String[]> list = resultService.getCountResultById(resultId);
        return ResultUtil.success(list);
    }

    @ResponseBody
    @RequestMapping("/deleteSets")
    public Object delSets(int[] sets, HttpServletRequest request) {
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

    @ResponseBody
    @RequestMapping("/statOnCurrentSet")
    public Object statOnCurrentSet(int set, HttpServletRequest request) {
        return null;
    }

}
