package com.xyt.xojbackendquestionservice.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentDeleteRequest  implements Serializable {


    /**
     * 题目id
     */
    private Long id;


    private static final long serialVersionUID = 1L;
}
