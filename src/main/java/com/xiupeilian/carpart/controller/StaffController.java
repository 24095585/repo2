package com.xiupeilian.carpart.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiupeilian.carpart.model.SysUser;
import com.xiupeilian.carpart.service.UserService;
import com.xiupeilian.carpart.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private UserService userService;
    /**
     * 员工信息首页
     * */
    @RequestMapping("/staffList")
    public String staffList(LoginVo vo, Integer pageSize, Integer pageNum, HttpServletRequest request) {
        SysUser user=(SysUser) request.getSession().getAttribute("user");
        vo.setCompanyId(user.getCompanyId());
        pageSize=pageSize==null?9:pageSize;
        pageNum=pageNum==null?1:pageNum;
        PageHelper.startPage(pageNum,pageSize);
        //查询全部
        List<SysUser> stafflist=userService.findUsers(vo);
        PageInfo<SysUser> page=new PageInfo<>(stafflist);

        request.setAttribute("page",page);
        request.setAttribute("staffList",stafflist);
        request.setAttribute("username",vo.getUsername());

        return "staff/staffList";
    }




}
