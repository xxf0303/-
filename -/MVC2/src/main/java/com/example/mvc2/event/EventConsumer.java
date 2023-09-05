package com.example.mvc2.event;

import com.alibaba.fastjson2.JSONObject;
import com.example.mvc2.dao.elasticsearch.DiscussPostRepository;
import com.example.mvc2.entity.DiscussPost;
import com.example.mvc2.entity.Event;
import com.example.mvc2.entity.Message;
import com.example.mvc2.service.DiscussPostService;
import com.example.mvc2.service.ElasticSearchService;
import com.example.mvc2.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticSearchService elasticSearchService;
    @KafkaListener(topics = {"comment","like","follow"})
    public void handleCommentMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null) {
            logger.error("消息格式错误");
            return;
        }
        //发送站内通知
        Message message = new Message();
        message.setFromId(1);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<Object, Object> map = new HashMap<>();
        map.put("userId",event.getUserId());
        map.put("entityType",event.getEntityType());
        map.put("entityId",event.getEntityId());

        if(!event.getData().isEmpty()) {
            for(Map.Entry<String,Object> entry : event.getData().entrySet()) {
                map.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(map));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = "publish")
    public void handlePublishMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null) {
            logger.error("消息格式错误");
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);
    }

}
