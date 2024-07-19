package com.xyt.xojbackendjudgeservice.codessandbox;


import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeRequest;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeResponse;

/**
 * 代码沙箱的接口定义
 */
public interface CodeSandBox {
    /**
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
