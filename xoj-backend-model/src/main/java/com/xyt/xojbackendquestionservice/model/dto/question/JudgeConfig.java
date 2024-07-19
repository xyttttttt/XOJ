package com.xyt.xojbackendquestionservice.model.dto.question;

import lombok.Data;

@Data
public class JudgeConfig {

    /**
     * 时间限制  ms
     * */
    private Long timeLimit;

    /**
     * 内层限制  kb
     * */
    private Long memoryLimit;


    /**
     * 堆栈限制  kb
     * */
    private Long stackLimit;

}
