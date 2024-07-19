package com.xyt.xojbackendquestionservice.controller.inner;

import com.xyt.oxibackendserviceclient.service.QuestionFeignClient;
import com.xyt.xojbackendquestionservice.model.entity.Question;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import com.xyt.xojbackendquestionservice.service.QuestionService;
import com.xyt.xojbackendquestionservice.service.QuestionSubmitService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    QuestionService questionService;

    @Resource
    QuestionSubmitService questionSubmitService;
    /**
     * 根据id查询question
     * @param questionId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId){
        return questionService.getById(questionId);
    }

    /**
     * 根据id查询题目提交信息
     * @param questionSubmitId
     * @return
     */
    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    /**
     * 更新题目提交信息
     * @param questionSubmit
     * @return
     */
    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit){
        return questionSubmitService.updateById(questionSubmit);
    }

    /**
     * 更新题目提交数和通过数
     * @param question
     * @return
     */
    @PostMapping("/update")
    public boolean updateQuestionById(@RequestBody Question question){
        return questionService.updateById(question);
    }

    /**
     * 删除题目提交信息
     */
    @DeleteMapping("/question_submit/delete/id")
    public boolean deleteSubmitById(@RequestParam("questionSubmitId") long questionSubmitId){
        return questionSubmitService.removeById(questionSubmitId);
    }

    @PostMapping("/question/getFavourNum/id")
    public int getQuestionFavourNum(@RequestParam("questionId") long questionId){
        return questionService.getQuestionFavourNum(questionId);
    }

    @PostMapping("/question/getThumbNum/id")
    public int getQuestionThumbNum(@RequestParam("questionId") long questionId){
        return questionService.getQuestionThumbNum(questionId);
    }


    @PostMapping("/question/updateQuestionFavourNum/id/status")
    public boolean updateQuestionFavourNum(@RequestParam("questionId") long questionId,@RequestParam("status") boolean status){
        return questionService.updateQuestionFavourNum(questionId,status);
    }

    @PostMapping("/question/updateQuestionThumbNum/id/status")
    public boolean updateQuestionThumbNum(@RequestParam("questionId") long questionId,@RequestParam("status") boolean status){
        return questionService.updateQuestionThumbNum(questionId,status);
    }
}
