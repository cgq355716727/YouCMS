package com.myside.service;

import java.util.Set;

public interface IRoleService {
    Set<String> getRoles(int userID);
}
