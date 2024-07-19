package com.xyt.xojbackendquestionfavourthumbservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.xojbackendquestionservice.model.entity.QuestionFavour;
import com.xyt.xojbackendquestionservice.model.vo.QuestionFavourVO;
import com.xyt.xojbackendquestionservice.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 16048
*/
public interface QuestionFavourService extends IService<QuestionFavour> {

    QuestionFavourVO changeQuestionFavour(Long questionId, HttpServletRequest request);

    Page<QuestionVO> listUserQuestionFavourByPage(Page<QuestionFavour> favourPage, Long userId);
}
