package com.example.mvc2.controller;

import com.example.mvc2.entity.*;
import com.example.mvc2.event.EventProducer;
import com.example.mvc2.service.CommentService;
import com.example.mvc2.service.DiscussPostService;
import com.example.mvc2.service.LikeService;
import com.example.mvc2.service.UserService;
import com.example.mvc2.util.CommunityUtil;
import com.example.mvc2.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content) {
        //先从holder取user
        User user = hostHolder.getUser();
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setContent(content);
        post.setTitle(title);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //触发发帖事件
        Event event = new Event()
                .setTopic("publish")
                .setUserId(user.getId())
                .setEntityType(1)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,"发布成功");
    }

    @RequestMapping(path = "/detail/{postid}",method = RequestMethod.GET)
    public String getDetailPage(@PathVariable("postid") int postid, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(postid);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("post",post);
        model.addAttribute("user",user);
        //点赞
        long likeCount = likeService.findEntityLikeCount(1, postid);
        model.addAttribute("likeCount",likeCount);
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),1,postid);
        model.addAttribute("likeStatus",likeStatus);

        page.setPath("/discuss/detail/" + postid);
        page.setLimit(5);
        page.setRows(post.getCommentCount());
        //当前帖子的所有评论
        List<Comment> commentList = commentService.findCommentsByEntity(1, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null) {
            for (Comment comment : commentList) {
                Map<String,Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                long likeCount2 = likeService.findEntityLikeCount(2, comment.getId());
                commentVo.put("likeCount",likeCount2);
                int likeStatus2 = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),2,comment.getId());
                commentVo.put("likeStatus",likeStatus2);
                //评论的评论
                List<Comment> replyList = commentService.findCommentsByEntity(2, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null) {
                    for (Comment reply : replyList) {
                        HashMap<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        long likeCount3 = likeService.findEntityLikeCount(2, reply.getId());
                        replyVo.put("likeCount",likeCount3);
                        int likeStatus3 = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),2,reply.getId());
                        replyVo.put("likeStatus",likeStatus3);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                //回复数量
                int i = commentService.countCommentsByEntity(2, comment.getId());
                commentVo.put("replyCount",i);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }

}
