package com.example.mvc2.service;

import com.example.mvc2.dao.CommentMapper;
import com.example.mvc2.dao.DiscussPostMapper;
import com.example.mvc2.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit) {
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    public int countCommentsByEntity(int entityType,int entityId) {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    public Comment findCommentById(int id) {
        return commentMapper.findCommentById(id);
    }

    //事务管理
    @Transactional(isolation= Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment) {
        if(comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        int i = commentMapper.insertComment(comment);
        //如果这条评论是给帖子的评论
        if(comment.getEntityType() == 1) {
           int count = commentMapper.selectCountByEntity(1, comment.getEntityId());
           discussPostService.updateCommentCount(comment.getEntityId(),count);
       }
        return i;
    }
}
