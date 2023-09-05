package com.example.mvc2.controller;

import com.example.mvc2.entity.DiscussPost;
import com.example.mvc2.entity.Page;
import com.example.mvc2.service.ElasticSearchService;
import com.example.mvc2.service.LikeService;
import com.example.mvc2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        org.springframework.data.domain.Page<DiscussPost> searchResults = elasticSearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(searchResults != null) {
            for (DiscussPost searchResult : searchResults) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",searchResult);
                map.put("user",userService.findUserById(searchResult.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(1,searchResult.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);

        page.setPath("/search?keyword=" +keyword);
        page.setRows(searchResults == null?0:(int)searchResults.getTotalElements());
        return "/site/search";
    }

}
