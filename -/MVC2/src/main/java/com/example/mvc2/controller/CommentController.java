package com.example.mvc2.controller;

import com.example.mvc2.entity.Comment;
import com.example.mvc2.entity.DiscussPost;
import com.example.mvc2.entity.Event;
import com.example.mvc2.event.EventProducer;
import com.example.mvc2.service.CommentService;
import com.example.mvc2.service.DiscussPostService;
import com.example.mvc2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private  DiscussPostService discussPostService;
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        commentService.insertComment(comment);

        //触发评论事件
        Event event = new Event().setTopic("comment")
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);

        if(comment.getEntityType() == 1) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == 2) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if(comment.getEntityType() == 1) {
            //触发发帖事件
            event = new Event()
                    .setTopic("publish")
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(1)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }
        return "redirect:/discuss/detail/" + discussPostId;
    }


}
