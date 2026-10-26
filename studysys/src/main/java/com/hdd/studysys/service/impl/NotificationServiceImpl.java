package com.hdd.studysys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hdd.studysys.entity.Notification;
import com.hdd.studysys.mapper.NotificationMapper;
import com.hdd.studysys.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void send(String userType, Integer userId, String note) {
        Notification notification = new Notification();
        notification.setUserType(userType);
        notification.setUserId(userId);
        notification.setNote(note);
        notificationMapper.insert(notification);
    }

    @Override
    public int unreadCount(String userType, Integer userId) {
        return notificationMapper.countUnread(userType, userId);
    }

    @Override
    public List<Notification> list(String userType, Integer userId) {
        return notificationMapper.selectByUser(userType, userId);
    }

    @Override
    public boolean markRead(Integer noteId, String userType, Integer userId) {
        return notificationMapper.markRead(noteId, userType, userId) > 0;
    }

    @Override
    public int markAllRead(String userType, Integer userId) {
        return notificationMapper.markAllRead(userType, userId);
    }
}
