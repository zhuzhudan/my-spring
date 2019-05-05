package com.study.spring.business.service.impl;

import com.study.spring.business.service.IQueryService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryService implements IQueryService {
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";

        return json;
    }
}
