package com.xyt.xojbackendjudgeservice.codessandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xyt.xojbackendjudgeservice.codessandbox.CodeSandBox;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeRequest;
import com.xyt.xojbackendquestionservice.model.codesandBox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;


/**
 * 远程代码沙箱
 */
public class RemoteCodeSandBoxImpl implements CodeSandBox {


    //定义鉴权请求头和密钥
    public static final String AUTH_REQUEST_HEADER = "auth-xyt";

    public static final String AUTH_REQUEST_SECRET = "5eafa9b7-7c7c-42cc-92ce-c43abfd29b6f";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://192.168.204.129:8122/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String response = HttpUtil.createPost(url)
                                    .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                                    .body(json)
                                    .execute()
                                    .body();
        if (StringUtils.isBlank(response)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"executeCode remoteCodeSandBox error,message = "+response);
        }
        return JSONUtil.toBean(response,ExecuteCodeResponse.class);
    }
}
