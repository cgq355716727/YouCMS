package com.myside.controller;

import com.myside.entity.U_User;
import com.myside.service.Impl.UserService;
import com.myside.util.EndecryptUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private PasswordService passwordService;

    @RequestMapping("login")
    public ModelAndView login(){
        return new ModelAndView("admin/login");
    }

    //验证用户
    @RequestMapping("validatelogin")
    public String validatelogin(String username, String password, String remember,HttpServletRequest request,Model model){
        System.out.println("----表单输入-----username="+username);
        System.out.println("-----表单输入----password="+password);
        try{
            Subject subject = SecurityUtils.getSubject();
            //passwordService=new DefaultPasswordService();


            String saltpassword=username+"#"+password;

            System.out.println("Password Service=" +passwordService.encryptPassword(saltpassword));
            System.out.println("getMD5=" +EndecryptUtils.getMD5(saltpassword));

            UsernamePasswordToken token = new UsernamePasswordToken(username, saltpassword);
            subject.login(token);
            if (subject.isAuthenticated()) {
                model.addAttribute("message", "登录成功");
            } else {
                model.addAttribute("message", "用户名/密码错误");
            }
            return "redirect:/admin/index";
            //return new ModelAndView("admin/index");
        }catch (Exception e){
            //   如果发生异常
            model.addAttribute("status", 500);
            model.addAttribute("message", "登录错误");
            //return new ModelAndView("admin/login");
            return "redirect:/admin/login";

        }

    }

    @RequestMapping("index")
    public String index(){
        return "admin/index";
    }
}
