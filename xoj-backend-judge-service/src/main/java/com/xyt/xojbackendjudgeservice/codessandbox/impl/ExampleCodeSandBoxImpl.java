package com.xyt.xojbackendjudgeservice.codessandbox.impl;


import com.xyt.xojbackendjudgeservice.codessandbox.CodeSandBox;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeRequest;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeResponse;
import com.xyt.xojbackendquestionservice.model.codesandBox.JudgeInfo;
import com.xyt.xojbackendquestionservice.model.enums.JudgeInfoMessageEnum;
import com.xyt.xojbackendquestionservice.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱
 */
public class ExampleCodeSandBoxImpl implements CodeSandBox {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        
        System.out.println("实例代码沙箱");
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(1000L);
        judgeInfo.setTime(1000L);

        executeCodeResponse.setJudgeInfo(judgeInfo);
        
        return executeCodeResponse;
    }
}
