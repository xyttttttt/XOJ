package com.xyt.xojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.xojbackendquestionservice.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xyt.xojbackendquestionservice.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackendquestionservice.model.vo.QuestionSubmitInfoVo;
import com.xyt.xojbackendquestionservice.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author 16048
* @description 针对表【question_submit(题目提交表)】的数据库操作Service
* @createDate 2023-10-10 21:03:42
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

/*    *//**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     *//*
    int doQuestionSubmitInner(long userId, long postId);*/

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *  登录用户提交题目记录信息
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);


    QuestionSubmitInfoVo userQuestionSubmitInfo(HttpServletRequest request);
}
