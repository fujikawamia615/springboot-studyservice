package com.hdd.user_service.mapper;

import java.util.List;

import com.hdd.user_service.entity.User;

public interface UserMapper {
    public List<User> selectAll();

    public User selectById(Integer id);

    public User selectByUsername(String username);

    public int insert(User user);

    public int update(User user);

    public int deleteById(Integer id);
}
