package com.myside.service;

import com.myside.entity.U_User;

import java.util.Set;

public interface IUserService {
     U_User getByNickname(String username);
     Set<String> getRoles(Long userid);
     Set<String> getPermissions(Long userid);

}


