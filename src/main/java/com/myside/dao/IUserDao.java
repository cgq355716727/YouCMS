package com.myside.dao;

import com.myside.entity.U_User;

import java.util.Set;

public interface IUserDao {

    U_User getByUsername(String username);

    Set<String> getRoles(String username);

    Set<String> getPermissions(String username);
}
