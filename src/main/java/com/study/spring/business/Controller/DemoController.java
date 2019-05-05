package com.study.spring.business.controller;

import com.study.spring.business.service.IDemoService;
import com.study.spring.business.service.IQueryService;
import com.study.spring.framework.annotation.MyAutowired;
import com.study.spring.framework.annotation.MyController;
import com.study.spring.framework.annotation.MyRequestMapping;
import com.study.spring.framework.annotation.MyRequestParam;
import com.study.spring.framework.webmvc.servlet.MyModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@MyController
@MyRequestMapping("/demo")
public class DemoController {
    @MyAutowired
    private IDemoService demoService;

    @MyAutowired
    private IQueryService queryService;

    @MyRequestMapping("/query")
    public MyModelAndView query(HttpServletRequest req, HttpServletResponse resp,
                                @MyRequestParam("name") String name){
        String result = queryService.query(name);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @MyRequestMapping("/add*")
    public MyModelAndView add(HttpServletRequest req, HttpServletResponse resp,
                    @MyRequestParam("name") String name, @MyRequestParam("addr")String addr){
        String result = null;
        try {
            result = demoService.add(name, addr);
            resp.getWriter().write(result);
            return null;
        } catch (Exception e) {
//            e.printStackTrace();
            Map<String, Object> model = new HashMap<>();
            model.put("detail", e.getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", ""));
            return new MyModelAndView("500", model);
        }
    }

    @MyRequestMapping("/remove")
    public MyModelAndView remove(HttpServletRequest req, HttpServletResponse resp,
                       @MyRequestParam("id") Integer id){
        String result =demoService.remove(id);
        return out(resp, result);

    }

    public MyModelAndView edit(HttpServletRequest req, HttpServletResponse resp,
                               @MyRequestParam("id")Integer id, @MyRequestParam("name")String name){
        String result = demoService.edit(id, name);
        return out(resp, result);
    }

    private MyModelAndView out(HttpServletResponse resp, String result) {
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
