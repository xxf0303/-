package com.example.mvc2.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_FOLLOWEE = "followee";

    private static final String PREFIX_KAPTCHA = "kaptcha";
    //某个实体的赞
    //like:entity:entityType:entityId ->set(userId)
    public static String getEntityLikeKey(int entityType,int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个用户收到的赞
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户被关注
    public static String getUserFollower(int userId) {
        return PREFIX_FOLLOWER + userId;
    }

    //某个用户关注的实体
    //followee:userId:entityType ->zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //关注某个实体的用户
    //follower:entityType:entityId ->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }
}
