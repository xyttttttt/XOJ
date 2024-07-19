package com.xyt.xojbackendquestionfavourthumbservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.oxibackendserviceclient.service.QuestionFeignClient;
import com.xyt.oxibackendserviceclient.service.UserFeignClient;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionfavourthumbservice.mapper.QuestionFavourMapper;
import com.xyt.xojbackendquestionfavourthumbservice.service.QuestionFavourService;
import com.xyt.xojbackendquestionservice.model.entity.Question;
import com.xyt.xojbackendquestionservice.model.entity.QuestionFavour;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackendquestionservice.model.vo.QuestionFavourVO;
import com.xyt.xojbackendquestionservice.model.vo.QuestionVO;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 16048
 * @description 针对表【question_favour(帖子收藏)】的数据库操作Service实现
 * @createDate 2023-11-21 16:42:33
 */
@Service
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour> implements QuestionFavourService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private Redisson redisson;

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Override
    public QuestionFavourVO changeQuestionFavour(Long questionId, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        Long userId = loginUser.getId();
        QuestionFavourVO questionFavourVO = new QuestionFavourVO();
        LambdaQueryWrapper<QuestionFavour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionFavour::getQuestionId, questionId);
        wrapper.eq(QuestionFavour::getUserId, userId);
        boolean status = true;
        RLock lock = redisson.getLock("question-add-favourNum");
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    QuestionFavour questionFavour = baseMapper.selectOne(wrapper);
                    if (questionFavour != null) {  //取消收藏
                        status = false;
                        int i = baseMapper.deleteById(questionFavour);
                        if (i == 0){
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                        }
                    } else {
                        questionFavour = new QuestionFavour();
                        questionFavour.setQuestionId(questionId);
                        questionFavour.setUserId(userId);
                        int insert = baseMapper.insert(questionFavour);
                        if (insert == 0){
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                        }
                    }
                    boolean updateFavourNum = questionFeignClient.updateQuestionFavourNum(questionId, status);
                    if (!updateFavourNum){
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                    }
                }
                finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        questionFavourVO.setStatus(status);
        int questionFavourNum = questionFeignClient.getQuestionFavourNum(questionId);
        questionFavourVO.setFavourNum(questionFavourNum);
        return questionFavourVO;
    }

    @Override
    public Page<QuestionVO> listUserQuestionFavourByPage(Page<QuestionFavour> favourPage, Long userId) {

        List<QuestionFavour> questionFavourList = favourPage.getRecords();
        //定义返回结果
        Page<QuestionVO> questionVOPage = new Page<>(favourPage.getCurrent(), favourPage.getSize(), favourPage.getTotal());
        if (questionFavourList == null){ //如果用户没有喜欢列表，直接返回
            return questionVOPage;
        }
        //根据用户id
        List<QuestionVO> questionVOList = questionFavourList.stream().map(questionFavour -> {
            Long questionId = questionFavour.getQuestionId();
            Question question = questionFeignClient.getQuestionById(questionId);
            QuestionVO questionVO = new QuestionVO();
            if (question != null){
                questionVO = QuestionVO.objToVo(question);
            }
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }


}




