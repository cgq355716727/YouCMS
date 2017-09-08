package com.myside.controller;

import com.myside.entity.U_User;
import com.myside.exception.CustomException;
import com.myside.service.Impl.UserService;
import com.myside.util.EndecryptUtils;
import com.myside.util.VerifyCodeUtils;
import com.myside.util.vcode.Captcha;
import com.myside.util.vcode.GifCaptcha;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
//    @Autowired
//    private PasswordService passwordService;
    private final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping(value = "login",method = RequestMethod.GET)
    public ModelAndView loginindex(){
        return new ModelAndView("admin/login");
    }

    //验证用户
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String validatelogin(String username, String password, String remember,HttpServletRequest request,Model model) {
        logger.info("进入控制器---validatelogin");
        System.out.println("----表单输入-----FormAuthenticationFilter=" + FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        System.out.println("----表单输入-----username=" + username);
        System.out.println("-----表单输入----password=" + password);

        //org.apache.shiro.crypto.hash.SimpleHash();
        //SimpleHash(String algorithmName, Object source, Object salt)

        String simpleHashcode = new SimpleHash("MD5", password).toString();
        System.out.println("simpleHashcode=" + simpleHashcode);

        simpleHashcode = new SimpleHash("MD5", password, username).toString();
        String saltpassword = username + "#" + password;
        System.out.println("simpleHashcode salt=" + simpleHashcode);

        //System.out.println("Password Service=" +passwordService.encryptPassword(saltpassword));
        System.out.println("getMD5=" + EndecryptUtils.getMD5(saltpassword));
        boolean rememberMe = false;
        String error = null;
        Subject currentUser = SecurityUtils.getSubject();
        System.out.println("验证码："+currentUser.getSession().getAttribute(VerifyCodeUtils.V_CODE));
//        currentUser.isRemembered()
        if (!currentUser.isAuthenticated()) {

            // 设置记住密码
            if (remember.equals("1")) {
                rememberMe = true;
            }

//          UsernamePasswordToken token = new UsernamePasswordToken(username, saltpassword);
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);

            System.out.println("isRememberMe =" + token.isRememberMe());
            try {
                // 执行登录. 调用 Subject 的 login(UsernamePasswordToken) 方法.
                currentUser.login(token);

            } catch (UnknownAccountException e) {
                error = "用户名/密码错误";
            } catch (IncorrectCredentialsException e) {
                error = "用户名/密码错误";
            } catch (ExcessiveAttemptsException e) {
                // TODO: handle exception
                error = "登录失败多次，账户锁定10分钟";
            } catch (AuthenticationException e) {
                // 其他错误，比如锁定，如果想单独处理请单独catch处理
                error = "其他错误：" + e.getMessage();
            }

        }
        if (error != null) {// 出错了，返回登录页面
            System.out.println("登录失败: " + error);
            model.addAttribute("message", error);
            return "admin/login";
        }
        // 登录成功
        return "admin/index";
    }


    @RequestMapping("login2")
    public String login1(@RequestParam("username") String username,
                        @RequestParam("password") String password){
        String error = null;
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            // 把用户名和密码封装为 UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // rememberme
            token.setRememberMe(true);
            try {
                // 执行登录.
                currentUser.login(token);
            }catch (UnknownAccountException e) {
                error = "用户名/密码错误";
            } catch (IncorrectCredentialsException e) {
                error = "用户名/密码错误";
            } catch (ExcessiveAttemptsException e) {
                // TODO: handle exception
                error = "登录失败多次，账户锁定10分钟";
            } catch (AuthenticationException e) {
                // 其他错误，比如锁定，如果想单独处理请单独catch处理
                error = "其他错误：" + e.getMessage();
            }

            if (error != null) {// 出错了，返回登录页面
                System.out.println("登录失败: " + error);
                //model.addAttribute("message", error);
                // 登录成功
                return "admin/login";
            }
            //model.addAttribute("message", "登录成功");
            return "admin/index";


        }

        return "redirect:/admin/index";
    }

    @RequestMapping("index")
    public String index(){
        return "admin/index";
    }


    //用户登陆提交方法  通过异常判断显示错误信息
    @RequestMapping("validatelogin")
    public  String loginshiro(HttpServletRequest request, String username, String password,Model model){


        //如果登录失败从request中获取认证异常信息，shrioLoginFailure就是shiro异常类的全限定名
        //String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        Subject subject = SecurityUtils.getSubject();

        System.out.println("验证码："+subject.getSession().getAttribute(VerifyCodeUtils.V_CODE));

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        String error = null;
        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
            error = "用户名/密码错误";
        } catch (IncorrectCredentialsException e) {
            error = "用户名/密码错误";
        } catch (ExcessiveAttemptsException e) {
            // TODO: handle exception
            error = "登录失败多次，账户锁定10分钟";
        } catch (AuthenticationException e) {
            // 其他错误，比如锁定，如果想单独处理请单独catch处理
            error = "其他错误：" + e.getMessage();
        } catch (Exception e) {
            // 其他错误，比如锁定，如果想单独处理请单独catch处理
            error = "其他错误：" + e.getMessage();
        }
        if (error != null) {// 出错了，返回登录页面
            model.addAttribute("message", error);
            // 登录成功
            return "admin/login";
        }
        model.addAttribute("message", "登录成功");
        return "admin/index";
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


    /***
     * 生成验证码
     * @param response
     * @param request
     */
    @RequestMapping("GifCode")
    public void getGifCode(HttpServletResponse response, HttpServletRequest request){
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/gif");
            /**
             * gif格式动画验证码
             * 宽，高，位数。
             */
            Captcha captcha = new GifCaptcha(146,42,4);
            //输出
            ServletOutputStream out = response.getOutputStream();
            captcha.out(out);
            out.flush();
            //存入Shiro会话session
            Session session=SecurityUtils.getSubject().getSession();
            session.setAttribute(VerifyCodeUtils.V_CODE, captcha.text().toLowerCase());

            //TokenManager.setVal2Session(VerifyCodeUtils.V_CODE, captcha.text().toLowerCase());
        } catch (Exception e) {
           // LoggerUtils.fmtError(getClass(),e, "获取验证码异常：%s",e.getMessage());

        }
    }
}
