package com.usian.feign;

import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-content-service")
public interface ContentServiceFeign {

    @RequestMapping("/service/content/selectContentCategoryByParentId")
    List<TbContentCategory> selectContentCategoryByParentId(@RequestParam Long id);

    @RequestMapping("/service/content/insertContentCategory")
    Integer insertContentCategory(@RequestParam Integer parentId, @RequestParam String name);

    @RequestMapping("/service/content/deleteContentCategoryById")
    Integer deleteContentCategoryById(@RequestParam Long categoryId);

    @RequestMapping("/service/content/updateContentCategory")
    Integer updateContentCategory(@RequestParam Long id, @RequestParam String name);

    @RequestMapping("/service/content/selectTbContentAllByCategoryId")
    PageResult selectTbContentAllByCategoryId(@RequestParam Long categoryId,@RequestParam Integer page, @RequestParam Integer rows);

    @RequestMapping("/service/content/insertTbContent")
    Integer insertTbContent(TbContent tbContent);

    @RequestMapping("/service/content/deleteContentByIds")
    Integer deleteContentByIds(@RequestParam Long ids);

    @RequestMapping("/service/content/selectFrontendContentByAD")
    List<AdNode> selectFrontendContentByAD();
}
