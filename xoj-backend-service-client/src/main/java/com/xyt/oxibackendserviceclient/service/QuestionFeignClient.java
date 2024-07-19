package com.xyt.oxibackendserviceclient.service;


import com.xyt.xojbackendquestionservice.model.entity.Question;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


/**
* @author 16048
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-10-10 21:02:35
*/
@FeignClient(value = "xoj-backend-question-service",path = "/api/question/inner")
public interface QuestionFeignClient {



    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);


    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);


    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);


    @PostMapping("/update")
    boolean updateQuestionById(@RequestBody Question question);

    @DeleteMapping("/question_submit/delete/id")
    boolean deleteSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);

    @PostMapping("/question/getFavourNum/id")
    int getQuestionFavourNum(@RequestParam("questionId") long questionId);

    @PostMapping("/question/getThumbNum/id")
    int getQuestionThumbNum(@RequestParam("questionId") long questionId);


    @PostMapping("/question/updateQuestionFavourNum/id/status")
    boolean updateQuestionFavourNum(@RequestParam("questionId") long questionId,@RequestParam("status") boolean status);

    @PostMapping("/question/updateQuestionThumbNum/id/status")
    boolean updateQuestionThumbNum(@RequestParam("questionId") long questionId,@RequestParam("status") boolean status);
}
