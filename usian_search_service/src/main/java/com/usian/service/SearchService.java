package com.usian.service;

import com.usian.pojo.SearchItem;

import java.util.List;

public interface SearchService {

    boolean importAll();

    List<SearchItem> selectByq(String q, Long page, Integer pagesize);

    int addDocement(String msg);
}
