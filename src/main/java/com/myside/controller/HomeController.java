package com.myside.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Controller注解，通过配置自动注册
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    //映射一个action
    @RequestMapping("/")
    public  String index(){
        //输出日志文件
        logger.info("the first jsp pages");
        //返回一个index.jsp这个视图
        return "index";
    }


    @RequestMapping(value="hello",method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {

        //绑定变量
        model.addAttribute("message", "Hello world jsp!");
        //返回hello，则视图解析器会自动加前后缀，最后解析到/WEB-INF/pages/hello.jsp
        return "hello";
    }


}
