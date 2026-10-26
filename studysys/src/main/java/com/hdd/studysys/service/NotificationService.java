package com.hdd.studysys.service;

import java.util.List;
import com.hdd.studysys.entity.Notification;

public interface NotificationService {
    void send(String userType, Integer userId, String note);

    int unreadCount(String userType, Integer userId);

    List<Notification> list(String userType, Integer userId);

    boolean markRead(Integer noteId, String userType, Integer userId);

    int markAllRead(String userType, Integer userId);
}
