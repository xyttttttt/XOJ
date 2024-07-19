package com.xyt.xojbackendquestionservice.model.dto.questionfavour;


import com.xyt.xojbackendjudgeservice.common.PageRequest;
import com.xyt.xojbackendquestionservice.model.dto.question.QuestionQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子收藏查询请求
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionFavourQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}