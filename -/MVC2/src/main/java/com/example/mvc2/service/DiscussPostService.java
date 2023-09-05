package com.example.mvc2.service;

import com.example.mvc2.dao.DiscussPostMapper;
import com.example.mvc2.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
       return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int uderId) {
        return discussPostMapper.selectDiscussPostRows(uderId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
       if(discussPost == null) {
           throw new IllegalArgumentException("参数不能为空！");
       }
       return discussPostMapper.insertDiscussPosts(discussPost);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id,int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }
}
