package com.example.mvc2.controller;

import com.example.mvc2.annotation.LoginRequired;
import com.example.mvc2.entity.User;
import com.example.mvc2.service.FollowService;
import com.example.mvc2.service.LikeService;
import com.example.mvc2.service.UserService;
import com.example.mvc2.util.CommunityUtil;
import com.example.mvc2.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private String uploadPath = "D:/Clash";
    private String domain = "http://localhost:8080";
    private String contextPath = "";
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if(headerImage == null) {
            model.addAttribute("error","您还没有选择照片");
            return "/site/setting";
        }
       String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        //存放文件的路径
        File dest = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException(e);
        }
        //更新头像路径
        //http://localhost:8080/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename")String filename, HttpServletResponse response) {
        filename = uploadPath + "/" + filename;
        //后缀
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix);
        try {
            ServletOutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(filename);
            byte[] bytes = new byte[1024];
            int b = 0;
            while((b = fis.read(bytes)) != -1) {
                os.write(bytes,0,b);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model) {
        int userLikeCount = likeService.findUserLikeCount(userId);
        User userById = userService.findUserById(userId);
        if(userById == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("likeCount",userLikeCount);
        model.addAttribute("user",userById);

        //关注数量
        long followeesCount = followService.findFolloweesCount(userId, 3);
        //粉丝数量
        long followersCount = followService.findFollowersCount(3, userId);
        //是否已关注当前用户
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), 3, userId);
        }
        model.addAttribute("followeeCount",followeesCount);
        model.addAttribute("followerCount",followersCount);
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
