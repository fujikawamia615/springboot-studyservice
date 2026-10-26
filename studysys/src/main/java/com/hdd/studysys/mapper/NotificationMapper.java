package com.hdd.studysys.mapper;

import java.util.List;
import com.hdd.studysys.entity.Notification;
import org.apache.ibatis.annotations.Param;

public interface NotificationMapper {
    int insert(Notification notification);

    int countUnread(@Param("userType") String userType, @Param("userId") Integer userId);

    List<Notification> selectByUser(@Param("userType") String userType, @Param("userId") Integer userId);

    int markRead(@Param("noteId") Integer noteId, @Param("userType") String userType,
            @Param("userId") Integer userId);

    int markAllRead(@Param("userType") String userType, @Param("userId") Integer userId);
}
