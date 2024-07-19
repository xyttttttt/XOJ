package com.xyt.xojbackendquestionservice.model.codesandBox;

import com.xyt.xojbackendquestionservice.model.dto.question.JudgeConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {

    private List<String> inputList;

    private String code;

    private String language;

    //时间限制、内存限制可加可不加
    private JudgeConfig judgeConfig;
}
