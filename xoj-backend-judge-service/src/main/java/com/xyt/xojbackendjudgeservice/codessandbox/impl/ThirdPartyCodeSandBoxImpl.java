package com.xyt.xojbackendjudgeservice.codessandbox.impl;

import com.xyt.xojbackendjudgeservice.codessandbox.CodeSandBox;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeRequest;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandBoxImpl implements CodeSandBox {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
