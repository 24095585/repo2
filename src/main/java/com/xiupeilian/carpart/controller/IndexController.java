package com.xiupeilian.carpart.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiupeilian.carpart.model.*;
import com.xiupeilian.carpart.service.DymsnService;
import com.xiupeilian.carpart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private UserService userService;
    @Autowired
    private DymsnService dymsnService;


    @RequestMapping("index")
    public String index(){
        return  "index/index";
    }
    /**
     * 顶端页面
     * */
    @RequestMapping("top")
    public String top(HttpServletRequest request){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String now =sdf.format(new Date());
        request.setAttribute("now",now);
        return  "index/top";
    }

    @RequestMapping("/navigation")
    public  String navigation(HttpServletRequest request){
        //获取当前登录用户的id
        SysUser user=(SysUser) request.getSession().getAttribute("user");
        int id=user.getId();

        List<Menu> menuList=userService.findMenusById(id);
        request.setAttribute("menuList",menuList);
        return  "index/navigation";
    }


    /**
     * 动态消息展示
     * */
    @RequestMapping("dymsn")
    public String dymsn(HttpServletRequest request){

        List<Dymsn> list = dymsnService.findDymsns();
        request.setAttribute("list",list);
        return  "index/dymsn";
    }

    /**
     * 公告
     * */
    @RequestMapping("/notice")
    public String notice(Integer pageSize,Integer pageNum,HttpServletRequest request){
        pageSize=pageSize==null?10:pageSize;
        pageNum=pageNum==null?1:pageNum;
        PageHelper.startPage(pageNum,pageSize);
        //查询全部
        List<Notice> list=dymsnService.findNotice();
        PageInfo<Notice> page=new PageInfo<>(list);
        request.setAttribute("page",page);
        return "index/notice";
    }
    /**
     * 新闻
     * */
    @RequestMapping("/news")
    public String news(Integer pageSize,Integer pageNum,HttpServletRequest request){
        pageSize=pageSize==null?10:pageSize;
        pageNum=pageNum==null?1:pageNum;
        PageHelper.startPage(pageNum,pageSize);
        //查询全部
        List<News> list=dymsnService.findNews();
        PageInfo<News> page=new PageInfo<>(list);
        request.setAttribute("page",page);
        return "index/news";
    }

}
