package com.xyt.xojbackenduserservice.controller.inner;

import com.xyt.oxibackendserviceclient.service.UserFeignClient;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 该服务仅内部调用   不给前端调用
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;
    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") Long userId){
        return userService.getById(userId);
    }

    /**
     * 根据用户idList 查询用户列表
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList){
        List<User> userList = userService.listByIds(idList);
        return userList;
    }

}
