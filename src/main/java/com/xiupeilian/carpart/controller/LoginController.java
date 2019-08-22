package com.xiupeilian.carpart.controller;


import com.xiupeilian.carpart.constant.SysConstant;
import com.xiupeilian.carpart.model.SysUser;
import com.xiupeilian.carpart.service.UserService;
import com.xiupeilian.carpart.task.MailTask;
import com.xiupeilian.carpart.util.SHA1Util;
import com.xiupeilian.carpart.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private ThreadPoolTaskExecutor executor;

    @RequestMapping("toLogin")
    public String toLogin(){
        return  "login/login";
    }

    @RequestMapping("login")
    public void login(LoginVo vo, HttpServletRequest request, HttpServletResponse response) throws  Exception{


        String code=(String) request.getSession().getAttribute(SysConstant.VALIDATE_CODE);
        if (vo.getValidate().toUpperCase().equals(code.toUpperCase())){
            //验证码正确
            vo.setPassword(SHA1Util.encode(vo.getPassword()));
            SysUser user=userService.findUserByLoginNameAndPassword(vo);
            if (null==user){
                //未登录成功
                response.getWriter().write("2");
            }else {
                //登录成功
                request.getSession().setAttribute("user",user);

                response.getWriter().write("3");
            }

        }else {
            //验证码错误
            response.getWriter().write("1");

        }

    }

    /**
     * 非法访问页面
     * */
    @RequestMapping("/noauth")
    public String noauth(){
        return  "exception/noauth";
    }

    /**
     * 忘记密码
     * */
    @RequestMapping("/forgetPassword")
    public String forgetPassword(){
        return "login/forgetPassword";
    }

    /**
     * 找回密码
     * */
    @RequestMapping("/getPassword")
    public void getPassword(LoginVo vo,HttpServletResponse response) throws  Exception{

        SysUser user = userService.findUserByLoginNameAndEmail(vo);

        if (null==user){
            response.getWriter().write("1");
        }else{
            //生成新密码 0.01s
            String password=new Random().nextInt(899999)+100000+"";

            //修改数据库  0.5s
            user.setPassword(SHA1Util.encode(password));
            userService.updateUser(user);


            //发送邮件到用户邮箱  1s  将同步流程异步化 可以采用多线程
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom("tu18610679287@sina.com");
            message.setTo(user.getEmail());
            message.setSubject("修配连汽配市场密码找回功能:");
            message.setText("您的新密码是"+password);

           // mailSender.send(message);
            //创建一个任务
            MailTask mailTask=new MailTask(mailSender,message);
            //让线程池执行该任务
            executor.execute(mailTask);
            response.getWriter().write("2");

        }

    }

}
