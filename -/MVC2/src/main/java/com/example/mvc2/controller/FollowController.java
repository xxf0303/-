package com.example.mvc2.controller;

import com.example.mvc2.entity.DiscussPost;
import com.example.mvc2.entity.Event;
import com.example.mvc2.entity.Page;
import com.example.mvc2.entity.User;
import com.example.mvc2.event.EventProducer;
import com.example.mvc2.service.DiscussPostService;
import com.example.mvc2.service.FollowService;
import com.example.mvc2.service.UserService;
import com.example.mvc2.util.CommunityUtil;
import com.example.mvc2.util.HostHolder;
import com.example.mvc2.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    //关注
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        Event event = new Event().setTopic("follow")
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setUserId(user.getId())
                .setEntityUserId(userService.findUserById(entityId).getId());
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取关");
    }

    @RequestMapping(path = "/followee/{userId}", method = RequestMethod.GET)
    public String followee(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followee/" + userId);
        page.setRows((int) followService.findFolloweesCount(userId, 3));

        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (followees != null) {
            for (Map<String, Object> followee : followees) {
                User u = (User) followee.get("user");
                followee.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",followees);
        return "/site/followee";
    }

    //查询用户粉丝
    @RequestMapping(path = "/follower/{userId}", method = RequestMethod.GET)
    public String follower(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/follower/" + userId);
        page.setRows((int) followService.findFollowersCount(3,userId));

        List<Map<String, Object>> followers = followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if (followers != null) {
            for (Map<String, Object> follower : followers) {
                User u = (User) follower.get("user");
                follower.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",followers);
        return "/site/follower";
    }
    private boolean hasFollowed(int userId) {
        if(hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),3,userId);
    }
}
