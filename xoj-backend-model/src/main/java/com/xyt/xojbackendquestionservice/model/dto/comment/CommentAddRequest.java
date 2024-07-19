package com.xyt.xojbackendquestionservice.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class CommentAddRequest implements Serializable{


    /**
     * 题目id
     */
    private Long questionId;


    /**
     * 评论内容
     */
    private String comment;



    private static final long serialVersionUID = 1L;
}