package com.xyt.xojbackendquestionservice.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xyt.xojbackendquestionservice.model.dto.question.JudgeConfig;
import com.xyt.xojbackendquestionservice.model.entity.Comment;
import com.xyt.xojbackendquestionservice.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户
 * @TableName comment
 */
@Data
public class CommentVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建时间
     */
    private Date createTime;



    private UserVO userVO;

    private boolean like;

    private boolean hasLike;

    /**
     * 
     */
    private Long parentCommentId;

    private static final long serialVersionUID = 1L;



    /**
     * 对象转包装类
     *
     * @param comment
     * @return
     */
    public static CommentVO objToVo(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);

        return commentVO;
    }
}