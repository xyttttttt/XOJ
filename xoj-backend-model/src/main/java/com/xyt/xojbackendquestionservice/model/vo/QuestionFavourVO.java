package com.xyt.xojbackendquestionservice.model.vo;

import lombok.Data;

@Data
public class QuestionFavourVO {

    private boolean status;

    private Long userId;

    private int favourNum;
}
