package com.xyt.xojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;

import com.xyt.oxibackendserviceclient.service.QuestionFavourThumbFeignClient;
import com.xyt.oxibackendserviceclient.service.UserFeignClient;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.constant.CommonConstant;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendjudgeservice.exception.ThrowUtils;
import com.xyt.xojbackendjudgeservice.utils.SqlUtils;
import com.xyt.xojbackendquestionservice.model.dto.question.QuestionQueryRequest;
import com.xyt.xojbackendquestionservice.model.entity.*;
import com.xyt.xojbackendquestionservice.model.vo.QuestionAdminVo;
import com.xyt.xojbackendquestionservice.model.vo.QuestionVO;
import com.xyt.xojbackendquestionservice.mapper.QuestionMapper;
import com.xyt.xojbackendquestionservice.model.vo.UserVO;
import com.xyt.xojbackendquestionservice.service.QuestionService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 16048
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2023-10-10 21:02:35
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final static Gson GSON = new Gson();

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionFavourThumbFeignClient questionFavourThumbFeignClient;


    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类   用户可能根据那些字段查询 根据前端传来的请求对象 得到mybatis框架支持的查询
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();



        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
//        queryWrapper.orderByDesc("thumbNum");
//        queryWrapper.orderByDesc("favourNum");
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionVO.setUserVO(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser != null) {
            // 获取点赞
            QuestionThumb questionThumb = questionFavourThumbFeignClient.getThumb(question.getId(),loginUser);
            questionVO.setHasThumb(questionThumb != null);
            // 获取收藏
            QuestionFavour questionFavour = questionFavourThumbFeignClient.getFavour(question.getId(), loginUser);
            questionVO.setHasFavour(questionFavour != null);
        }
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));  //按用户id分组

        // 2. 已登录，获取用户点赞、收藏状态


        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }

            questionVO.setUserVO(userFeignClient.getUserVO(user));

            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public Page<QuestionAdminVo> getQuestionAdminVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionAdminVo> questionAdminVoPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionAdminVoPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));  //按用户id分组

        // 填充信息
        List<QuestionAdminVo> questionAdminVoList = questionList.stream().map(question -> {
            QuestionAdminVo questionAdminVo = QuestionAdminVo.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionAdminVo.setUserId(userId);
//            questionVO.setHasThumb(questionIdHasThumbMap.getOrDefault(question.getId(), false));
//            questionVO.setHasFavour(questionIdHasFavourMap.getOrDefault(question.getId(), false));
            return questionAdminVo;
        }).collect(Collectors.toList());
        questionAdminVoPage.setRecords(questionAdminVoList);
        return questionAdminVoPage;
    }

    @Override
    public int getQuestionFavourNum(long questionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getId,questionId);
        Question question = baseMapper.selectById(questionId);
        return question.getFavourNum();
    }

    @Override
    public int getQuestionThumbNum(long questionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getId,questionId);
        Question question = baseMapper.selectById(questionId);
        return question.getThumbNum();
    }

    @Override
    public boolean updateQuestionFavourNum(long questionId, boolean status) {
        Question question = new Question();
        question.setId(questionId);
        if (status){
            question.setFavourNum(this.getQuestionFavourNum(questionId) + 1);
        }else {
            question.setFavourNum(this.getQuestionFavourNum(questionId) - 1);
        }
        int update = baseMapper.updateById(question);
        if (update > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateQuestionThumbNum(long questionId, boolean status) {
        Question question = new Question();
        question.setId(questionId);
        if (status){
            question.setThumbNum(this.getQuestionThumbNum(questionId) + 1);
        }else {
            question.setThumbNum(this.getQuestionThumbNum(questionId) - 1);
        }
        int update = baseMapper.updateById(question);
        if (update > 0){
            return true;
        }
        return false;
    }


}




