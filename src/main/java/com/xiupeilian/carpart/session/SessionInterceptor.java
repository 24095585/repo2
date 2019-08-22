package com.xiupeilian.carpart.session;

import com.xiupeilian.carpart.model.Menu;
import com.xiupeilian.carpart.model.SysUser;
import com.xiupeilian.carpart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求的URL
        String path=request.getRequestURI();
        if (path.contains("login")){
            return true;
        }else {
            //获取session
            HttpSession session=request.getSession(false);
            if (null==session){
                //session为null,用户没有登录
                response.sendRedirect(request.getContextPath()+"/login/toLogin");
                return  false;
            }else {
                //获取用户的session
                SysUser user=(SysUser) session.getAttribute("user");
                if (null==user){
                    //没有用户session
                    response.sendRedirect(request.getContextPath()+"/login/toLogin");
                    return  false;
                }else {
                    //用户已登录
                    List<Menu> menuList = userService.findMenusById(user.getId());

                    boolean check=false;
                    for(Menu menu:menuList){
                        //如果用户请求的资源路径 包含了 自己所拥有的导航中的权限关键字
                        if (path.contains(menu.getMenuKey())){
                            check=true;
                        }
                    }
                    //如果check为true，那么就是正常访问，如果为false，非法访问
                    if (check){
                        return  true;
                    }else {
                        //登录成功状态，但是非法访问
                        response.sendRedirect(request.getContextPath()+"/login/noauth");
                        return  false;

                    }

                }

            }

        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            request.setAttribute("ctx",request.getContextPath());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
