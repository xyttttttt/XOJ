package com.xyt.xojbackendquestionfavourthumbservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.oxibackendserviceclient.service.QuestionFeignClient;
import com.xyt.oxibackendserviceclient.service.UserFeignClient;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionfavourthumbservice.mapper.QuestionThumbMapper;
import com.xyt.xojbackendquestionfavourthumbservice.service.QuestionThumbService;
import com.xyt.xojbackendquestionservice.model.entity.QuestionThumb;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackendquestionservice.model.vo.QuestionThumbVO;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author 16048
 * @description 针对表【question_submit(题目提交表)】的数据库操作Service实现
 * @createDate 2023-11-21 16:42:40
 */
@Service
public class QuestionThumbServiceImpl extends ServiceImpl<QuestionThumbMapper, QuestionThumb> implements QuestionThumbService {


    @Resource
    private UserFeignClient userFeignClient;
    @Autowired
    private Redisson redisson;
    @Resource
    private QuestionFeignClient questionFeignClient;

    @Override
    public QuestionThumbVO changeQuestionThumb(Long questionId, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        Long userId = loginUser.getId();
        QuestionThumbVO questionThumbVO = new QuestionThumbVO();
        LambdaQueryWrapper<QuestionThumb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionThumb::getQuestionId, questionId);
        wrapper.eq(QuestionThumb::getUserId, userId);
        boolean status = true;
        RLock lock = redisson.getLock("question-add-thumbNum");
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    QuestionThumb questionThumb = baseMapper.selectOne(wrapper);
                    if (questionThumb != null) { //操作为取消收藏
                        status = false;  //取消点赞
                        //删除表内容
                        int i = baseMapper.deleteById(questionThumb);
                        if (i == 0){
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"操作失败");
                        }
                    } else {
                        questionThumb = new QuestionThumb();
                        questionThumb.setQuestionId(questionId);
                        questionThumb.setUserId(userId);
                        int insert = baseMapper.insert(questionThumb);
                        if (insert == 0){
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"操作失败");
                        }
                    }
                    boolean updateThumbNum= questionFeignClient.updateQuestionThumbNum(questionId, status);
                    if (!updateThumbNum){
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"操作失败");
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        questionThumbVO.setStatus(status);
        int questionThumbNum = questionFeignClient.getQuestionThumbNum(questionId);
        questionThumbVO.setThumbNum(questionThumbNum);
        return questionThumbVO;
    }

}




