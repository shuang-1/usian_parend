package com.usian.controller;

import com.usian.pojo.SearchItem;
import com.usian.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/searchItem")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping("/importAll")
    public boolean importAll(){
        boolean b = searchService.importAll();
        return b;
    }

    @RequestMapping("/selectByq")
    public List<SearchItem> selectByq(String q, Long page, Integer pagesize){
        return searchService.selectByq(q,page,pagesize);
    }
}
