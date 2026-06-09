<template>
  <div>
    <h2>选课管理</h2>
    <el-row style="margin-bottom:15px">
      <el-select v-model="selectedSchedule" placeholder="选择课程安排" style="width:400px" clearable @change="fetch">
        <el-option v-for="s in schedules" :key="s.scheduleId"
          :label="s.courseName + ' - ' + s.teacherName + ' (周' + s.dayOfWeek + ' 第' + s.startPeriod + '~' + s.endPeriod + '节)'"
          :value="s.scheduleId" />
      </el-select>
    </el-row>
    <el-table :data="list" border stripe>
      <el-table-column prop="studentName" label="学生" />
      <el-table-column prop="courseName" label="课程" />
      <el-table-column prop="teacherName" label="教师" />
      <el-table-column label="时间">
        <template #default="{ row }">周{{ row.dayOfWeek }} 第{{ row.startPeriod }}~{{ row.endPeriod }}节</template>
      </el-table-column>
      <el-table-column prop="enrollTime" label="选课时间" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ENROLLED' ? 'success' : 'info'">{{ row.status === 'ENROLLED' ? '已选' : '已退' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!selectedSchedule && !list.length" description="请先选择课程安排" />
    <el-empty v-if="selectedSchedule && !list.length" description="该课程暂无学生选课" />
  </div>
</template>

<script>
import api from '../api'
export default {
  data() {
    return { list: [], schedules: [], selectedSchedule: null }
  },
  async mounted() {
    this.schedules = (await api.get('/course-schedule')).data
  },
  methods: {
    async fetch() {
      if (!this.selectedSchedule) { this.list = []; return }
      this.list = (await api.get(`/enrollment/schedule/${this.selectedSchedule}`)).data
    }
  }
}
</script>
