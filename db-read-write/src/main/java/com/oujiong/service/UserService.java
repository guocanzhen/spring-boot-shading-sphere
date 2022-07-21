package com.oujiong.service;

import com.oujiong.entity.User;

import java.util.List;

/**
 * @author xub
 * @Description: 用户相关接口
 * @date 2019/8/24 下午6:32
 */
public interface UserService {

    /**
     * 获取所有用户信息
     * @return 获取所有用户信息
     */
    List<User> list();

    /**
     * 用戶
     * @param user 用戶
     * @return 用戶
     * 用戶
     */
    String saveOne(User user);

}