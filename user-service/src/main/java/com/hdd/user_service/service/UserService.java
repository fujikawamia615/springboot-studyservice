package com.hdd.user_service.service;

import com.github.pagehelper.PageInfo;
import com.hdd.user_service.entity.User;

public interface UserService {
    PageInfo<User> selectAll(Integer page, Integer size);

    User selectById(Integer userId);

    int insert(User user);

    int update(User user);

    int deleteById(Integer userId);

    int register(User user);

    String login(String username, String password);
}
