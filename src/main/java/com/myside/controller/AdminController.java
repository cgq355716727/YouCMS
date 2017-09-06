package com.myside.controller;

import com.myside.entity.U_User;
import com.myside.exception.CustomException;
import com.myside.service.Impl.UserService;
import com.myside.util.EndecryptUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {
//    @Autowired
//    private PasswordService passwordService;

    @RequestMapping("login")
    public ModelAndView login(){
        return new ModelAndView("admin/login");
    }

    //验证用户
    @RequestMapping("validatelogin")
    public String validatelogin(String username, String password, String remember,HttpServletRequest request,Model model){
        System.out.println("----表单输入-----username="+FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        System.out.println("----表单输入-----username="+username);
        System.out.println("-----表单输入----password="+password);

        //org.apache.shiro.crypto.hash.SimpleHash();
        //SimpleHash(String algorithmName, Object source, Object salt)

        String simpleHashcode =new SimpleHash("MD5", password).toString();
        System.out.println("simpleHashcode=" +simpleHashcode);

        simpleHashcode =new SimpleHash("MD5", password, username).toString();
        String saltpassword=username+"#"+password;
        System.out.println("simpleHashcode salt=" +simpleHashcode);

        //System.out.println("Password Service=" +passwordService.encryptPassword(saltpassword));
        System.out.println("getMD5=" +EndecryptUtils.getMD5(saltpassword));
        boolean rememberMe=false;

        Subject currentUser = SecurityUtils.getSubject();
//        currentUser.isRemembered()
        if(!currentUser.isAuthenticated()){

            // 设置记住密码
            if (remember.equals("1")){
                rememberMe=true;
            }

//          UsernamePasswordToken token = new UsernamePasswordToken(username, saltpassword);
            UsernamePasswordToken token = new UsernamePasswordToken(username, password,rememberMe);
            token.setRememberMe(true);
            System.out.println("isRememberMe ="+ token.isRememberMe());
            try {
                // 执行登录. 调用 Subject 的 login(UsernamePasswordToken) 方法.
                currentUser.login(token);

            } catch (AuthenticationException ae) {

                //System.out.println("登录失败：" + ae.getMessage());
                model.addAttribute("message", "用户名/密码错误");
                return "admin/login";

            }
        }
        model.addAttribute("message", "登录成功");
        return "admin/index";
    }


    @RequestMapping("login2")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password){

        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            // 把用户名和密码封装为 UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // rememberme
            token.setRememberMe(true);
            try {
                // 执行登录.
                currentUser.login(token);
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            // 所有认证时异常的父类.
            catch (AuthenticationException ae) {
                //unexpected condition?  error?
                System.out.println("登录失败: " + ae.getMessage());
            }
        }

        return "redirect:/admin/index";
    }

    @RequestMapping("index")
    public String index(){
        return "admin/index";
    }


    //用户登陆提交方法  通过异常判断显示错误信息
    @RequestMapping("/validatelogin1")
    public  String loginshiro(HttpServletRequest request)throws Exception{
        //如果登录失败从request中获取认证异常信息，shrioLoginFailure就是shiro异常类的全限定名
        //String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        String exceptionClassName = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        //根据shrio返回的异常路径判断，抛出指定异常信息
        if (exceptionClassName!=null){
            if(UnknownAccountException.class.getName().equals(exceptionClassName)){
                //抛出异常
                throw new CustomException("账户不存在");
            }else if(IncorrectCredentialsException.class.getName().equals(exceptionClassName)){
                throw new CustomException("用户名/密码错误");
            }else if("randomCodeError".equals(exceptionClassName)){
                throw new CustomException("验证码错误");
            } else {
                throw new Exception("未知错误");
            }
        }
        return "admin/login";
    }

    /**
     * 退出
     */
    @RequestMapping("logout")
    public Object logout() {
        //logger.info("登出");
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/admin/login";
    }

}
