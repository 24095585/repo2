package com.xiupeilian.carpart.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.xiupeilian.carpart.constant.SysConstant;
import com.xiupeilian.carpart.model.*;
import com.xiupeilian.carpart.service.BrandService;
import com.xiupeilian.carpart.service.CityService;
import com.xiupeilian.carpart.service.UserService;
import com.xiupeilian.carpart.task.MailTask;
import com.xiupeilian.carpart.util.SHA1Util;
import com.xiupeilian.carpart.vo.LoginVo;
import com.xiupeilian.carpart.vo.RegisterVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CityService cityService;
    @Autowired
    private RedisTemplate jedis;

    @RequestMapping("toLogin")
    public String toLogin(){
        return  "login/login";
    }

    @RequestMapping("login")
    public void login(LoginVo vo, HttpServletRequest request, HttpServletResponse response) throws  Exception{


        String code=(String) request.getSession().getAttribute(SysConstant.VALIDATE_CODE);
        if (vo.getValidate().toUpperCase().equals(code.toUpperCase())){
            //验证码正确
            Subject subject= SecurityUtils.getSubject();
            UsernamePasswordToken token=new UsernamePasswordToken(vo.getLoginName(),vo.getPassword());

            try {
                subject.login(token);
            }catch (Exception e){
                //用户名密码错误
                response.getWriter().write(e.getMessage());
                return;
            }
            //获取存入的用户信息
            SysUser user=(SysUser) SecurityUtils.getSubject().getPrincipal();
            //存入spring-session
            request.getSession().setAttribute("user",user);
            response.getWriter().write("3");

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
    /**
     * 跳转注册页面 展示品牌种类，配件种类，精品种类
     * */

    @RequestMapping("/toRegister")
    public String register(HttpServletRequest request){
        List<Brand> brandList=brandService.findBrandsAll();
        List<Parts> partsList=brandService.findPartsAll();
        List<Prime> primeList=brandService.findPrimesAll();

        request.setAttribute("brandList",brandList);
        request.setAttribute("partsList",partsList);
        request.setAttribute("primeList",primeList);
        return "login/register";
    }

    /**
     * 验证登录账号
     * */
    @RequestMapping("/checkLoginName")
    public void checkLoginName(String loginName,HttpServletResponse response)throws  Exception{

        SysUser user=userService.findUserByLoginName(loginName);
        if (null==user){
            response.getWriter().write("1");
        }else {
            response.getWriter().write("2");
        }
    }

    /**
     * 验证手机号
     * */
    @RequestMapping("/checkPhone")
    public void checkPhone(String telnum,HttpServletResponse response)throws  Exception{

        SysUser user=userService.findUserByPhone(telnum);
        if (null==user){
            response.getWriter().write("1");
        }else {
            response.getWriter().write("2");
        }
    }

    /**
     * 验证邮箱
     * */
    @RequestMapping("/checkEmail")
    public void checkEmail(String email,HttpServletResponse response)throws  Exception{

        SysUser user=userService.findUserByEmail(email);
        if (null==user){
            response.getWriter().write("1");
        }else {
            response.getWriter().write("2");
        }
    }

/**
 * 验证企业名称
 * */
    @RequestMapping("/checkCompanyname")
    public void checkCompanyname(String companyname,HttpServletResponse response)throws  Exception{

        Company company=userService.findCompanyByCompanyname(companyname);
        if (null==company){
            response.getWriter().write("1");
        }else {
            response.getWriter().write("2");
        }
    }


    /**
     * 省市县三级联动
     * */
    @RequestMapping("/getCity")
    public @ResponseBody List<City> getCity(Integer parentId){
        parentId=parentId==null?SysConstant.CITY_CHINA_ID:parentId;
        List<City> cityList=cityService.findCitiesByParentId(parentId);
        return cityList;
    }

    /**
     * 注册功能
     * */
    @RequestMapping("/register")
    public String register(RegisterVo vo){
        userService.addRegister(vo);
        return  "redirect:toLogin";
    }

    @RequestMapping("/toView")
    public  String view(){
        return "login/sms";
    }

    @RequestMapping("/smsControllter")
    public void smsControllter(String phone) {

        DefaultProfile profile = DefaultProfile.getProfile("default", "LTAIzHHwD1lkUDsX", "sSaindqb4UK3eEZBJoao6CHUiMzO1S");
        IAcsClient client = new DefaultAcsClient(profile);
        String code=new Random().nextInt(899999)+100000+"";
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "\u4fee\u914d\u8fde");
        request.putQueryParameter("TemplateCode", "SMS_172884079");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());

            jedis.boundValueOps(phone).set(code);

            jedis.expire(phone,2, TimeUnit.MINUTES);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/smsQuery")
    public void  smsQuery(String code,String phone,HttpServletResponse response) throws  Exception{

        String code1=(String) jedis.boundValueOps(phone).get();

        if (code==null||!code.equals(code1)){
            response.getWriter().write("3");
        }else if (code.equals(code1)){
            response.getWriter().write("2");
        }else if(code1==null){
            response.getWriter().write("1");
        }

    }

    @RequestMapping("/test")
    public void test(Integer id){
        cityService.deleteCityById(id);
    }

}
