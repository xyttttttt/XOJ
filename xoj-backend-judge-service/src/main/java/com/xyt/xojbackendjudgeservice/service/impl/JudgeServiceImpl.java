package com.xyt.xojbackendjudgeservice.service.impl;


import cn.hutool.json.JSONUtil;

import com.xyt.oxibackendserviceclient.service.QuestionFeignClient;
import com.xyt.xojbackendjudgeservice.strategy.JudgeContext;
import com.xyt.xojbackendjudgeservice.service.JudgeManager;
import com.xyt.xojbackendjudgeservice.service.JudgeService;
import com.xyt.xojbackendjudgeservice.codessandbox.CodeSandBox;
import com.xyt.xojbackendjudgeservice.codessandbox.CodeSandboxFactory;
import com.xyt.xojbackendjudgeservice.codessandbox.CodeSandboxProxy;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeRequest;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeResponse;
import com.xyt.xojbackendquestionservice.model.codesandBox.JudgeInfo;
import com.xyt.xojbackendquestionservice.model.dto.question.JudgeCase;
import com.xyt.xojbackendquestionservice.model.dto.question.JudgeConfig;
import com.xyt.xojbackendquestionservice.model.entity.Question;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import com.xyt.xojbackendquestionservice.model.enums.QuestionSubmitStatusEnum;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;


    @Autowired
    private JudgeManager judgeManager;

    @Autowired
    private Redisson redisson;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1.传入题目提交的id 获取题目、提交信息(包含代码、语言等)
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        //如果提交题目信息不存在
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "数据异常，请重试");
        }

        Long questionId = questionSubmit.getQuestionId();
        //如果题目为空
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            questionFeignClient.deleteSubmitById(questionSubmitId);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交异常，请重试");
        }

        //判断题目状态防止重复执行
        //如果正在判题中 返回并抛出异常
//        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
//            questionFeignClient.deleteSubmitById(questionSubmitId);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目正在判题中");
//        }
        //更改题目状态   防止在等待判题的过程中，用户重复提交
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "服务判题异常");
        }
        //2.调用代码沙箱、获取执行结果
        //根据用户想要的判题模式 创建对应的沙箱
        CodeSandBox codeSandBox = CodeSandboxFactory.newInstance(type);
        //增强代理类 (在执行代码沙箱前后打印日志：请求信息和响应信息)
        codeSandBox = new CodeSandboxProxy(codeSandBox);

        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        //获取输入用例    判题用例
        String judgeCaseStr = question.getJudgeCase();
        String judgeConfigStr = question.getJudgeConfig();

        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);

        //输入用例转数组
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        //封装请求信息并执行代码沙箱   返回响应结果
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .judgeConfig(judgeConfig)
                .build();

        //更新question表  更新题目提交数   采用乐观锁读，写锁更新数据库
        StampedLock stampedLock = new StampedLock();
        long l = stampedLock.tryOptimisticRead();
        try {
            question = questionFeignClient.getQuestionById(questionId);
            if (!stampedLock.validate(l)) {
                l = stampedLock.readLock();
                try {
                    question = questionFeignClient.getQuestionById(questionId);
                } finally {
                    stampedLock.unlockRead(l);
                }
            }
            l = stampedLock.writeLock();
            try {
                question.setSubmitNum(question.getSubmitNum() + 1);
                boolean isUpdate = questionFeignClient.updateQuestionById(question);
                if (!isUpdate) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题库失败");
                }
            } finally {
                stampedLock.unlockWrite(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        //如果响应信息为null 则说明编译代码失败
        if (executeCodeResponse.getOutputList() == null) {
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage("编译失败");
            judgeInfo.setMemory(0L);
            judgeInfo.setTime(0L);
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "服务判题异常");
            }
        } else {
            //在响应结果中获取用户代码输出信息
            List<String> outputList = executeCodeResponse.getOutputList();

            //3.根据代码沙箱的执行结果、设置题目的判题状态和信息
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
            judgeContext.setInputList(inputList);
            judgeContext.setOutputList(outputList);
            judgeContext.setJudgeCaseList(judgeCaseList);
            judgeContext.setQuestion(question);
            judgeContext.setQuestionSubmit(questionSubmit);

            JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

            //更新数据库
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "服务判题异常");
            }
            String message = judgeInfo.getMessage();

            //更新question表  更新题目通过数   使用redisson分布式锁
            RLock redissonLock = redisson.getLock("question_accept");
            try {
                if (redissonLock.tryLock(5, TimeUnit.SECONDS)) {  //最多尝试获取锁等待五秒
                    try {
                        question = questionFeignClient.getQuestionById(questionId);
                        Integer acceptedNum = question.getAcceptedNum();
                        if (message.equals("成功")) {
                            question.setAcceptedNum(acceptedNum + 1);
                            boolean updateQuestionById = questionFeignClient.updateQuestionById(question);
                            if (!updateQuestionById) {
                                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题库失败");
                            }
                        }
                    } catch (Exception e) {
                        // 处理各种可能出现的异常
                        throw new RuntimeException(e);
                    } finally {
                        redissonLock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }
}
