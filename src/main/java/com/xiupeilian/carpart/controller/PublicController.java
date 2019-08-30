package com.xiupeilian.carpart.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiupeilian.carpart.model.Brand;
import com.xiupeilian.carpart.model.Items;
import com.xiupeilian.carpart.model.Parts;
import com.xiupeilian.carpart.service.BrandService;
import com.xiupeilian.carpart.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private BrandService brandService;

    @RequestMapping("/publicItems")
    public String publicItems(Items items, Integer pageSize, Integer pageNo, HttpServletRequest request,String brandName) throws Exception{

        pageSize=pageSize==null?8:pageSize;
        pageNo=pageNo==null?1:pageNo;
        //分页
        PageHelper.startPage(pageNo,pageSize);
        //多条件查询
        List<Items> itemsList=itemsService.findItemsByQueryVo(items);

        PageInfo<Items> page=new PageInfo<>(itemsList);
        //查询品牌
        List<Brand> brandList=brandService.findBrandsAll();
        //查询配件类别
        List<Parts> partsList=brandService.findPartsAll();

        request.setAttribute("brandList",brandList);
        request.setAttribute("partsList",partsList);
        request.setAttribute("page",page);
        request.setAttribute("items",items);
        request.setAttribute("brandName",brandName);

        return "public/publicItems";
    }

}
