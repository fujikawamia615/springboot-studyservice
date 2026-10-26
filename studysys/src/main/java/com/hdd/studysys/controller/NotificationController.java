package com.hdd.studysys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hdd.studysys.entity.Notification;
import com.hdd.studysys.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/unread-count")
    @Operation(summary = "未读通知数")
    public Map<String, Object> unreadCount(@RequestHeader("role") String role,
            @RequestHeader("referenceId") Integer referenceId) {
        return Map.of("count", notificationService.unreadCount(role, referenceId));
    }

    @GetMapping
    @Operation(summary = "我的通知列表")
    public List<Notification> list(@RequestHeader("role") String role,
            @RequestHeader("referenceId") Integer referenceId) {
        return notificationService.list(role, referenceId);
    }

    @PutMapping("/{noteId}/read")
    @Operation(summary = "标记一条通知为已读")
    public Map<String, Object> markRead(@PathVariable Integer noteId,
            @RequestHeader("role") String role,
            @RequestHeader("referenceId") Integer referenceId) {
        if (!notificationService.markRead(noteId, role, referenceId))
            return Map.of("success", false, "message", "通知不存在");
        return Map.of("success", true, "message", "已读");
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记为已读")
    public Map<String, Object> markAllRead(@RequestHeader("role") String role,
            @RequestHeader("referenceId") Integer referenceId) {
        return Map.of("success", true, "count", notificationService.markAllRead(role, referenceId));
    }
}
