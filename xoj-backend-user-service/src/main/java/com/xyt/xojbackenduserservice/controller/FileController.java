package com.xyt.xojbackenduserservice.controller;

import com.xyt.xojbackendjudgeservice.common.BaseResponse;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.common.ResultUtils;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackenduserservice.service.FileService;
import com.xyt.xojbackenduserservice.service.UserService;
import com.xyt.xojbackenduserservice.utils.UserAvatarImage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "FileController")
@RestController
@RequestMapping("/")
public class FileController {

    @Resource
    private FileService fileService;
    @Autowired
    private UserService userService;
    /**
     * 上传头像
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public BaseResponse<UserAvatarImage> uploadOssFile(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        //获取上传的文件
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "上传文件为空");
        }
        //返回上传到oss的路径

        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        UserAvatarImage userAvatarImage = fileService.uploadFileAvatar(file,userId);
        //返回r对象
        return ResultUtils.success(userAvatarImage);
    }
}