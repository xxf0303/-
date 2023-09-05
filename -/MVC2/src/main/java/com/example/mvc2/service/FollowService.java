package com.example.mvc2.service;

import com.example.mvc2.dao.UserMapper;
import com.example.mvc2.entity.Page;
import com.example.mvc2.entity.User;
import com.example.mvc2.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    public void follow(int userId,int entityType,int entityId) {
            //一次业务有两次存取，要保证事务型
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                operations.multi();

                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    public void unfollow(int userId,int entityType,int entityId) {
        //一次业务有两次存取，要保证事务型
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                operations.multi();

                operations.opsForZSet().remove(followerKey,userId);
                operations.opsForZSet().remove(followeeKey,entityId);

                return operations.exec();
            }
        });
    }
    //查询某用户关注的实体的数量
    public long findFolloweesCount(int userId,int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return  redisTemplate.opsForZSet().zCard(followeeKey);
    }
    
    //查询某实体被多少个用户关注
    public long findFollowersCount(int entityType,int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId,int entityType,int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    //查询某个用户关注的人，支持分页
    public List<Map<String,Object>> findFollowees(int userId, int offset,int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,3);
        Set<Integer> targetId = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(targetId == null) {
            return null;
        } else {
            List<Map<String,Object>> list = new ArrayList<>();
            for (Integer id : targetId) {
                Map<String,Object> map = new HashMap<>();
                User user = userMapper.selectById(id);
                map.put("user",user);
                Double score = redisTemplate.opsForZSet().score(followeeKey, id);
                map.put("followTime",new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }
    }

    //查询某个用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(3, userId);
        Set<Integer> targetId = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(targetId == null) {
            return null;
        } else {
            List<Map<String,Object>> list = new ArrayList<>();
            for (Integer id : targetId) {
                Map<String,Object> map = new HashMap<>();
                User user = userMapper.selectById(id);
                map.put("user",user);
                Double score = redisTemplate.opsForZSet().score(followerKey, id);
                map.put("followTime",new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }
    }
}
