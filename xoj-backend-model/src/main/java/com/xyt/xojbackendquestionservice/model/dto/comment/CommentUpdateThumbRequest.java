package com.xyt.xojbackendquestionservice.model.dto.comment;

import lombok.Data;

import java.io.Serializable;
@Data
public class CommentUpdateThumbRequest implements Serializable {

    private Long id;

    private boolean like;

    private static final long serialVersionUID = 1L;
}
