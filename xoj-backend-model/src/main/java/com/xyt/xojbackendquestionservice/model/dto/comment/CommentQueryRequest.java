package com.xyt.xojbackendquestionservice.model.dto.comment;

import com.xyt.xojbackendjudgeservice.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQueryRequest extends PageRequest implements Serializable {

     /**
     * id
     */
    private Long questionId;



    private static final long serialVersionUID = 1L;
}