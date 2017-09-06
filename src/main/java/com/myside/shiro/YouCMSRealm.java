package com.myside.shiro;

import com.myside.entity.U_User;
import com.myside.service.Impl.UserService;
//import com.myside.util.EndecryptUtils; 自己写的加密
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;


public class YouCMSRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    // 验证当前登录的用户，获取认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String) token.getPrincipal(); // 获取用户名
        U_User user = userService.getByNickname(username); //从数据库得到用户对象


        if(user != null) {
            System.out.println("YouCMSRealm密码userPswd="+user.getPswd());
            //email + '#' + pswd，然后MD5
            //AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user, user.getPswd(), ByteSource.Util.bytes(user.getNickname()), "YouCMSRealm");
            AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(username, "a66abb5684c45962d887564f08346e8d", ByteSource.Util.bytes(user.getNickname()), "YouCMSRealm");
            return authcInfo;
        } else {
            return null;
        }
    }


    // 为当前登陆成功的用户授予权限和角色，已经登陆成功了
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        //String username = (String) principals.getPrimaryPrincipal(); //获取用户名
//        U_User activeUser= (U_User) principals.getPrimaryPrincipal();
//        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

//        authorizationInfo.setRoles(userService.getRoles(activeUser.getId()));  //从数据库获取用户的角色
//        authorizationInfo.setStringPermissions(userService.getPermissions(activeUser.getId())); //从数据获取用户的权限
//        authorizationInfo.setRoles(roles);  //从数据库获取用户的角色
//
//
//        return authorizationInfo;


        //1. 从 PrincipalCollection 中来获取登录用户的信息
        Object principal = principals.getPrimaryPrincipal();

        //2. 利用登录的用户的信息来用户当前用户的角色或权限(可能需要查询数据库)
        Set<String> roles = new HashSet<String>();
        roles.add("user");
        if("admin".equals(principal)){
            roles.add("admin");
        }

        //3. 创建 SimpleAuthorizationInfo, 并设置其 reles 属性.
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);

        //4. 返回 SimpleAuthorizationInfo 对象.
        return info;

    }
}
