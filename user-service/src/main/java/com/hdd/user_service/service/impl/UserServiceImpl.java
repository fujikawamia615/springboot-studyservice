package com.hdd.user_service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hdd.user_service.entity.User;
import com.hdd.user_service.mapper.UserMapper;
import com.hdd.user_service.service.KafkaProducerService;
import com.hdd.user_service.service.UserService;
import com.hdd.user_service.util.JwtUtil;
import org.springframework.web.client.RestTemplate;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Autowired
    private JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public PageInfo<User> selectAll(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        return new PageInfo<>(userMapper.selectAll());
    }

    @Override
    public User selectById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public int deleteById(Integer userId) {
        return userMapper.deleteById(userId);
    }

    @Override
    public int register(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if ("teacher".equals(user.getRole())) {
            if (user.getReferenceId() == null) {
                throw new RuntimeException("教师ID不能为空");
            }
            String url = "http://localhost:8080/api/teacher/" + user.getReferenceId();
            if (restTemplate.getForObject(url, Object.class) == null) {
                throw new RuntimeException("教师ID不存在");
            }
        } else if ("student".equals(user.getRole())) {
            if (user.getReferenceId() == null) {
                throw new RuntimeException("学生ID不能为空");
            }
            String url = "http://localhost:8080/api/student/" + user.getReferenceId();
            if (restTemplate.getForObject(url, Object.class) == null) {
                throw new RuntimeException("学生ID不存在");
            }
        } else {
            throw new RuntimeException("只允许注册教师或学生账户");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insert(user);
    }

    @Override
    public String login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        kafkaProducerService.sendLoginEvent(user.getUserId(), user.getUsername());
        return jwtUtil.generateToken(user.getUserId(), user.getRole());
    }
}
