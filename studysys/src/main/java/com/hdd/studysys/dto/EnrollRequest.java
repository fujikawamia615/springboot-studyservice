package com.hdd.studysys.dto;

import java.io.Serializable;

public class EnrollRequest implements Serializable {
    private Integer studentId;
    private Integer scheduleId;

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
}