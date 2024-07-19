package com.xyt.xojbackenduserservice.service;

import com.xyt.xojbackenduserservice.utils.UserAvatarImage;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    UserAvatarImage uploadFileAvatar(MultipartFile file, Long userId);
}
