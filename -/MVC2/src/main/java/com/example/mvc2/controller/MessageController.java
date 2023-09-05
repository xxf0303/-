package com.example.mvc2.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.mvc2.entity.Message;
import com.example.mvc2.entity.Page;
import com.example.mvc2.entity.User;
import com.example.mvc2.service.MessageService;
import com.example.mvc2.service.UserService;
import com.example.mvc2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;
import sun.swing.BakedArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        //未读
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversations != null) {
            for (Message conversation : conversationList) {
                Map<String, Object> map = new HashMap<>();
                int unread = messageService.selectLetterUnreadCount(user.getId(), conversation.getConversationId());
                map.put("unreadCount", unread);
                map.put("conversation", conversation);
                map.put("letterCount", messageService.CountLetters(conversation.getConversationId()));
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //查询未读消息数
        int i = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("totalUnreadCount", i);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        //分页
        page.setLimit(5);
        page.setRows(messageService.CountLetters(conversationId));
        page.setPath("/letter/detail/" + conversationId);

        List<Message> messages = messageService.selectMessages(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                User user = userService.findUserById(message.getFromId());
                map.put("letter", message);
                map.put("fromUser", user);
                letters.add(map);
            }
        }
        model.addAttribute("target",getLetterTarget(conversationId));
        model.addAttribute("letters", letters);
        List<Integer> ids = getLetterIds(messages);
        if(!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] s = conversationId.split("_");
        int d0 = Integer.parseInt(s[0]);
        int d1 = Integer.parseInt(s[1]);
        int targetId = hostHolder.getUser().getId() == d0 ? d1 :d0;
        return userService.findUserById(targetId);

    }

    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        Message comment = messageService.findLatestNotice(user.getId(), "comment");

        if(comment != null) {
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message",comment);
            String content = HtmlUtils.htmlUnescape(comment.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageVO.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), "comment");
            messageVO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), "comment");
            messageVO.put("unread",unread);
            model.addAttribute("commentNotice",messageVO);
        }


        Message like = messageService.findLatestNotice(user.getId(), "like");

        if(like != null) {
            Map<String,Object> messageBO = new HashMap<>();
            messageBO.put("message",like);
            String content = HtmlUtils.htmlUnescape(like.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageBO.put("user",userService.findUserById((Integer)data.get("userId")));
            messageBO.put("entityType",data.get("entityType"));
            messageBO.put("entityId",data.get("entityId"));
            messageBO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), "like");
            messageBO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), "like");
            messageBO.put("unread",unread);
            model.addAttribute("likeNotice",messageBO);
        }


        Message follow = messageService.findLatestNotice(user.getId(), "follow");

        if(follow != null) {
            Map<String,Object> messageCO = new HashMap<>();
            messageCO.put("message",follow);
            String content = HtmlUtils.htmlUnescape(follow.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageCO.put("user",userService.findUserById((Integer)data.get("userId")));
            messageCO.put("entityType",data.get("entityType"));
            messageCO.put("entityId",data.get("entityId"));
            messageCO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), "follow");
            messageCO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), "follow");
            messageCO.put("unread",unread);
            model.addAttribute("followNotice",messageCO);
        }


        //查询未读消息量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "site/notice";
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVolist = new ArrayList<>();
        if(notices != null) {
            for (Message notice : notices) {
                Map<String,Object> map = new HashMap<>();
                map.put("notice",notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.findUserById((Integer)data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                map.put("fromUser",userService.findUserById(notice.getFromId()));

                noticeVolist.add(map);
            }
        }
        model.addAttribute("notices",noticeVolist);

        //设置已读
        List<Integer> ids = getLetterIds(notices);
        if(!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
