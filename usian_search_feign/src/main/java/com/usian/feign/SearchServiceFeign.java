package com.usian.feign;

import com.usian.pojo.SearchItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-search-service")
public interface SearchServiceFeign {

    @RequestMapping("/service/searchItem/importAll")
    boolean importAll();

    @RequestMapping("/service/searchItem/selectByq")
    List<SearchItem> selectByq(@RequestParam String q, @RequestParam Long page, @RequestParam Integer pagesize);
}
