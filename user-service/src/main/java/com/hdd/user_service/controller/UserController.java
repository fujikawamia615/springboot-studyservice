package com.hdd.user_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.hdd.user_service.dto.LoginDTO;
import com.hdd.user_service.entity.User;
import com.hdd.user_service.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public PageInfo<User> selectAll(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return userService.selectAll(page, size);
    }

    @GetMapping("/{id}")
    public User selectById(@PathVariable Integer id) {
        return userService.selectById(id);
    }

    @PostMapping("")
    public int insert(@RequestBody User user) {
        return userService.insert(user);
    }

    @PutMapping("")
    public int update(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public int deleteById(@PathVariable Integer id) {
        return userService.deleteById(id);
    }

    @PostMapping("/register")
    public int register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDTO dto) {
        String token = userService.login(dto.getUsername(), dto.getPassword());
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        return result;
    }
}
