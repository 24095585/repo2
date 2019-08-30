package com.xiupeilian.carpart.service;

import com.xiupeilian.carpart.model.Company;
import com.xiupeilian.carpart.model.Menu;
import com.xiupeilian.carpart.model.Role;
import com.xiupeilian.carpart.model.SysUser;
import com.xiupeilian.carpart.vo.LoginVo;
import com.xiupeilian.carpart.vo.RegisterVo;

import java.util.List;

public interface UserService {

    public SysUser findUserByLoginNameAndPassword(LoginVo vo);

    List<Menu> findMenusById(int id);

    SysUser findUserByLoginNameAndEmail(LoginVo vo);

     void updateUser(SysUser user);

    List<SysUser> findUsers(LoginVo vo);

    SysUser findUserByLoginName(String loginName);

    SysUser findUserByPhone(String phone);

    SysUser findUserByEmail(String email);

    Company findCompanyByCompanyname(String companyname);

    void addRegister(RegisterVo  vo);

    Role findRoleByRoleId(Integer id);
}
