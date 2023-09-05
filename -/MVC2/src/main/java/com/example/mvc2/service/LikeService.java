package com.example.mvc2.service;

import com.example.mvc2.util.HostHolder;
import com.example.mvc2.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HostHolder hostHolder;

    //点赞
    public void like(int userId,int entityType,int entityId,int entityUserId) {
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        Boolean member = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if(member == true) {
//            //已经点过赞
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        } else {
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean member = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                //开启事务
                operations.multi();
                if(member) {
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    //某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某个用户是否给某个实体点赞过
    public int findEntityLikeStatus(int userId,int entityType,int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    //查询某个用户被赞过的数量
    public int findUserLikeCount(int entityUserId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
