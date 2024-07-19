package com.xyt.xojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.oxibackendserviceclient.service.UserFeignClient;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.constant.CommonConstant;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendjudgeservice.mq.MqConstant;
import com.xyt.xojbackendjudgeservice.utils.SqlUtils;
import com.xyt.xojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.xyt.xojbackendquestionservice.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xyt.xojbackendquestionservice.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xyt.xojbackendquestionservice.model.entity.Question;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackendquestionservice.model.enums.QuestionSubmitLanguageEnum;
import com.xyt.xojbackendquestionservice.model.enums.QuestionSubmitStatusEnum;
import com.xyt.xojbackendquestionservice.model.vo.QuestionSubmitInfoVo;
import com.xyt.xojbackendquestionservice.model.vo.QuestionSubmitVO;
import com.xyt.xojbackendquestionservice.mq.MessageProducer;
import com.xyt.xojbackendquestionservice.service.QuestionService;
import com.xyt.xojbackendquestionservice.service.QuestionSubmitService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 16048
 * @description 针对表【question_submit(题目提交表)】的数据库操作Service实现
 * @createDate 2023-10-10 21:03:42
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MessageProducer messageProducer;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {

        // 校验编程语音是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setTitle(question.getTitle());
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        //发送消息
        //设置消息的 TTL 时间
        messageProducer.sendMessage(MqConstant.NORMAL_EXCHANGE, MqConstant.NORMAL_ROUTING_KEY, String.valueOf(questionSubmitId));
        return questionSubmitId;
    }




    /**
     * 获取查询包装类   用户可能根据那些字段查询 根据前端传来的请求对象 得到mybatis框架支持的查询
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        String sortField = questionSubmitQueryRequest.getSortField();

        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        // queryWrapper.eq("isDelete", false);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏 ： 仅本人和管理员能看见提交的代码
        Long userId = loginUser.getId();
        if (userId != questionSubmitVO.getUserId() && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit ->
                getQuestionSubmitVO(questionSubmit, loginUser)
        ).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    /**
     * 、
     * 获取用户
     *
     * @param request
     * @return
     */
    @Override
    public QuestionSubmitInfoVo userQuestionSubmitInfo(HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = loginUser.getId();
        QuestionSubmitInfoVo questionSubmitInfoVo = new QuestionSubmitInfoVo();
        LambdaQueryWrapper<QuestionSubmit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionSubmit::getUserId, userId);
        //提交总数
        Long submitTotal = baseMapper.selectCount(wrapper);
        questionSubmitInfoVo.setSubmitTotal(submitTotal);

        //通过的提交总数
        wrapper.like(QuestionSubmit::getJudgeInfo, "%成功%");
        Long submitAcceptTotal = baseMapper.selectCount(wrapper);
        questionSubmitInfoVo.setSubmitAcceptTotal(submitAcceptTotal);

        //题目通过总数
        //问题：使用groupby中所查询的列中包含id、不能分组
       // wrapper.groupBy(QuestionSubmit::getQuestionId);
        List<QuestionSubmit> questionSubmits = baseMapper.selectList(wrapper);
        int questionSubmitAcceptTotal = questionSubmits.stream().collect(Collectors.groupingBy(QuestionSubmit::getQuestionId)).size();
       // int questionSubmitAcceptTotal = questionSubmits.size();
        questionSubmitInfoVo.setQuestionSubmitAcceptTotal((long) questionSubmitAcceptTotal);

        //题目提交总数
        LambdaQueryWrapper<QuestionSubmit> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq(QuestionSubmit::getUserId, userId).groupBy(QuestionSubmit::getQuestionId);
        queryWrapper.eq(QuestionSubmit::getUserId, userId);
        List<QuestionSubmit> questionSubmitList = baseMapper.selectList(queryWrapper);
        int questionSubmitTotal = questionSubmitList.stream().collect(Collectors.groupingBy(QuestionSubmit::getQuestionId)).size();
        //int questionSubmitTotal = questionSubmitList.size();
        questionSubmitInfoVo.setQuestionSubmitTotal((long) questionSubmitTotal);
        return questionSubmitInfoVo;
    }

}




