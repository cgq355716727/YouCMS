package com.myside.shiro;

import com.myside.entity.U_User;
import com.myside.service.Impl.UserService;
import com.myside.util.EndecryptUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;


public class YouCMSRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    // 验证当前登录的用户，获取认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal(); // 获取用户名
        U_User user = userService.getByNickname(username); //从数据库得到用户对象
        System.out.println("数据库密码userPswd="+user.getPswd());

        if(user != null) {
            //email + '#' + pswd，然后MD5
            String password_input_md5=new EndecryptUtils().getMD5(user.getPswd());
            AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user, user.getPswd(), "YouCMSRealm");
            System.out.println("password_input_md5="+password_input_md5);
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
        U_User activeUser= (U_User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo.setRoles(userService.getRoles(activeUser.getId()));  //从数据库获取用户的角色
        authorizationInfo.setStringPermissions(userService.getPermissions(activeUser.getId())); //从数据获取用户的权限
        return authorizationInfo;
    }
}
