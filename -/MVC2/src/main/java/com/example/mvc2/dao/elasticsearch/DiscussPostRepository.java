package com.example.mvc2.dao.elasticsearch;

import com.example.mvc2.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchCrudRepository<DiscussPost,Integer> {
}
