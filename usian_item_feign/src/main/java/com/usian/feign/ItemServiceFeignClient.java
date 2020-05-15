package com.usian.feign;

import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-item-service")
public interface ItemServiceFeignClient {

    @RequestMapping("/service/item/selectItemInfo")
    TbItem selectItemInfo(@RequestParam Long itemId);


    //分页查询商品数据
    @RequestMapping("/service/item/selectTbItemAllByPage")
    PageResult selectTbItemAllByPage(@RequestParam Integer page, @RequestParam Integer rows);

    /**
     * 根据id查询商品的类目
     * @param id
     * @return
     */
    @RequestMapping("/service/itemCat/selectItemCategoryByParentId")
    List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long id);

    /**
     * 查询商品规格参数
     * @param itemCatId
     * @return
     */
    @RequestMapping("/service/itemParam/selectItemParamByItemCatId/{itemCatId}")
    TbItemParam selectItemParamByItemCatId(@PathVariable Long itemCatId);

    /**
     * 添加商品
     * @param tbItem
     * @param desc
     * @param itemParams
     * @return
     */
    @RequestMapping("/service/item/insertTbItem")
    Integer insertTbItem(@RequestBody TbItem tbItem, @RequestParam String desc, @RequestParam String itemParams);
}
