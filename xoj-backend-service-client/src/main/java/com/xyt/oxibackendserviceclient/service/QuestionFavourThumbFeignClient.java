package com.xyt.oxibackendserviceclient.service;

import com.xyt.xojbackendquestionservice.model.entity.QuestionFavour;
import com.xyt.xojbackendquestionservice.model.entity.QuestionThumb;
import com.xyt.xojbackendquestionservice.model.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * 帖子收藏服务
 */
@FeignClient(name = "xoj-backend-question-favourthumb-service",path = "/api/favour/thumb")
public interface QuestionFavourThumbFeignClient{

    @PostMapping("/inner/getFavourNum/id")
    QuestionFavour getFavour(@RequestParam("questionId") long questionId,@RequestBody User loginUser);

    /**
     * 获取点赞
     * @return
     */
    @PostMapping("/inner/getThumbNum/id")
    QuestionThumb getThumb(@RequestParam("questionId") long questionId,@RequestBody User loginUser);
}
