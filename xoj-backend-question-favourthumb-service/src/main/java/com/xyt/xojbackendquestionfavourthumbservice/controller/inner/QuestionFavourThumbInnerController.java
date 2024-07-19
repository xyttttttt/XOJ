package com.xyt.xojbackendquestionfavourthumbservice.controller.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xyt.oxibackendserviceclient.service.QuestionFavourThumbFeignClient;
import com.xyt.xojbackendquestionfavourthumbservice.service.QuestionFavourService;
import com.xyt.xojbackendquestionfavourthumbservice.service.QuestionThumbService;
import com.xyt.xojbackendquestionservice.model.entity.QuestionFavour;
import com.xyt.xojbackendquestionservice.model.entity.QuestionThumb;
import com.xyt.xojbackendquestionservice.model.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/inner")
public class QuestionFavourThumbInnerController implements QuestionFavourThumbFeignClient {

    @Resource
    private QuestionThumbService questionThumbService;

    @Resource
    private QuestionFavourService questionFavourService;

    @PostMapping("/getFavourNum/id")
    public QuestionFavour getFavour(@RequestParam("questionId") long questionId,@RequestBody User loginUser){
        LambdaQueryWrapper<QuestionFavour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionFavour::getQuestionId, questionId);
        wrapper.eq(QuestionFavour::getUserId,loginUser.getId());
        return  questionFavourService.getOne(wrapper);
    }

    /**
     * 获取点赞
     * @return
     */
    @PostMapping("/getThumbNum/id")
    public QuestionThumb getThumb(@RequestParam("questionId") long questionId, @RequestBody  User loginUser){
        LambdaQueryWrapper<QuestionThumb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionThumb::getQuestionId, questionId);
        wrapper.eq(QuestionThumb::getUserId,loginUser.getId());
        return  questionThumbService.getOne(wrapper);
    }
}
