package com.example.mvc2.dao;

import com.example.mvc2.entity.Comment;
import com.example.mvc2.service.CommentService;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);
    int selectCountByEntity(int entityType,int entityId);
    int insertComment(Comment comment);

    Comment findCommentById(int id);

}
