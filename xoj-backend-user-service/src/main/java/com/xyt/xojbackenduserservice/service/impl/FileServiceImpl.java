package com.xyt.xojbackenduserservice.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackenduserservice.service.FileService;
import com.xyt.xojbackenduserservice.service.UserService;
import com.xyt.xojbackenduserservice.utils.FileUtils;
import com.xyt.xojbackenduserservice.utils.UserAvatarImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Shier
 * 阿里云对象存储实现类
 */
@Service
public class FileServiceImpl implements FileService {


    @Autowired
    UserService userService;

    /**
     * 文件大小 10MB(可用于图片和视频区分)
     */
    private static final long FILE_SIZE = 8 * 1024 * 1024;

    /**
     * 只支持图片格式
     */
    public static final List<String> YES_IMAGE_SUPPORT = Arrays.asList(".jpg", ".jpeg", ".png");
    /**
     * 上传头像到OSS
     */
    @Override
    public UserAvatarImage uploadFileAvatar(MultipartFile file, Long userId) {

        //工具类获取值
        String endpoint = FileUtils.END_POINT;
        String accessKeyId = FileUtils.KEY_ID;
        String accessKeySecret = FileUtils.KEY_SECRET;
        String bucketName = FileUtils.BUCKET_NAME;
        InputStream inputStream = null;
        try {
            // 创建OSS实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            String originalFilename = file.getOriginalFilename();

            originalFilename = originalFilename.substring(originalFilename.lastIndexOf("."));

            if (!YES_IMAGE_SUPPORT.contains(originalFilename)){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"上传文件类型不正确");
            }
            // 检查文件大小
            int length = file.getBytes().length;
            if (file.getBytes().length > FILE_SIZE) { // 假设最大上传大小为5MB
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"最大上传大小为5MB");
            }

            //获取上传文件输入流
            inputStream = file.getInputStream();
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //
            fileName = userId+fileName;

            //2 把文件按照日期进行分类
            String userAvatar = "userAvatar";
            //拼接
            fileName = userAvatar+"/"+userId+"/"+fileName;


            UserAvatarImage userAvatarImage = new UserAvatarImage();
            userAvatarImage.setName(fileName);

            // 判断文件是否存在
            boolean exists = ossClient.doesObjectExist(bucketName, fileName);
            if (exists) {
                // 如果文件已存在，则先删除原来的文件再进行覆盖
                ossClient.deleteObject(bucketName, fileName);
            }


            //调用oss实例中的方法实现上传
            //参数1： Bucket名称
            //参数2： 上传到oss文件路径和文件名称 /aa/bb/1.jpg
            //参数3： 上传文件的输入流
            ossClient.putObject(bucketName, fileName, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();


            // 把上传后文件路径返回，需要把上传到阿里云oss路径手动拼接出来
            String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
            User user = new User();
            user.setId(userId);
            user.setUserAvatar(url);
            boolean updateUser = userService.updateById(user);
            if (!updateUser){
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            userAvatarImage.setUrl(url);
            return userAvatarImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

