package com.myside.service;

import java.util.Set;

public interface IPermission {

    Set<String> getPermissions(int userID);
}
