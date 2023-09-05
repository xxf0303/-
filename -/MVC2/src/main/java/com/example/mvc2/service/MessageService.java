package com.example.mvc2.service;

import com.example.mvc2.dao.MessageMapper;
import com.example.mvc2.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    public List<Message> findConversations(int userId,int offset,int limit) {
        return messageMapper.selectMessagesById(userId,offset,limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.CountMessageById(userId);
    }

    public List<Message> selectMessages(String conversationId,int offset,int limit) {
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    public int CountLetters(String conversationId) {
        return messageMapper.CountLetters(conversationId);
    }

    public int selectLetterUnreadCount(int userId,String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        return messageMapper.addMessage(message);
    }

    public Message findLatestNotice(int userId,String topic) {
        return messageMapper.selectLatestNotice(userId,topic);
    }

    public int findNoticeCount(int userId,String topic) {
        return messageMapper.selectNoticeCount(userId,topic);
    }

    public int findNoticeUnreadCount(int userId,String topic) {
        return messageMapper.selectNoticeUnreadCount(userId,topic);
    }

    public List<Message> findNotices(int userId,String topic,int offset,int limit) {
        return messageMapper.selectNotices(userId,topic,offset,limit);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }
}
