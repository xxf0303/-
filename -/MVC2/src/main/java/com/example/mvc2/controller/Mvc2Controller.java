package com.example.mvc2.controller;

import com.example.mvc2.service.Mvc2Service;
import com.example.mvc2.util.CommunityConstant;
import com.example.mvc2.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class Mvc2Controller {
    @Autowired
    private Mvc2Service mvc2Service;
    @RequestMapping("/service")
    @ResponseBody
    public String getData() {
        return mvc2Service.find();
    }
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello,SpringBoot";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String header = request.getHeader(name);
            System.out.println(name + " " + header);

        }
        System.out.println(request.getParameter("code"));

        //返回响应
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("<h1>好耶</h1>");
    }

    // /students?current=1&limit=20
    @RequestMapping(path="/students",method= RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",defaultValue = "1",required = false) int current, @RequestParam(name="limit",defaultValue = "10",required = false) int limit) {

        return "current = " + current + " limit = " + limit;
    }

    // /students/123
    @RequestMapping(path = "/students/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudnetById(@PathVariable("id") int id){
        return Integer.toString(id);
    }
    //POST
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(@RequestParam("name") String name,@RequestParam("age") int age) {
        return "name = " + name + ",age = " + age;

    }

    //响应HTML数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","xsada");
        modelAndView.addObject("age",1);
        modelAndView.setViewName("/demo/view");
        return modelAndView;

    }

    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String school(Model model) {
        model.addAttribute("name","yeye");
        model.addAttribute("age",13);
        return "/demo/view";
    }
    //响应JSON数据（异步响应）
    @RequestMapping(value = "/emp",method = RequestMethod.GET)
    @ResponseBody //返回JSON字符串
    public Map getemp() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("name","哈哈");
        map.put("age",233);
        map.put("salary",2222);
        return map;
    }

    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setcookie(HttpServletResponse response) {
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //cookie生效范围
        cookie.setPath("/");
        //cookie生存时间
        cookie.setMaxAge(60 * 10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    //session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //ajax示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String ajaxtest(String name,int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }
}
