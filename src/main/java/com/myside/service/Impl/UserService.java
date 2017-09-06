package com.myside.service.Impl;

import com.myside.dao.U_UserMapper;
import com.myside.entity.U_User;
import com.myside.service.IUserService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Service("userService")
public class UserService implements IUserService{

    @Resource
    private U_UserMapper userDao;

    public U_User getByNickname(String username) {
        return userDao.getByNickname(username);
    }

    public Set<String> getRoles(Long userid) {
        Set<String> userRole =new HashSet<String>();
        userRole.add("");
        return userRole;
        //return userDao.getRoles(username);
    }

    public Set<String> getPermissions(Long userid) {
        Set<String> userRole =new HashSet<String>();
        userRole.add("");
        return userRole;
        //return userDao.getPermissions(username);
    }

    //
    public void setPassword(){

    }

}
