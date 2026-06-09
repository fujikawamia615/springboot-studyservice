<template>
  <div>
    <h2>可选课程</h2>
    <el-row :gutter="20">
      <el-col v-for="s in schedules" :key="s.scheduleId" :span="8" style="margin-bottom:15px">
        <el-card>
          <p><strong>{{ s.courseName }}</strong> — {{ s.teacherName }}</p>
          <p>周{{ s.dayOfWeek }} 第{{ s.startPeriod }}~{{ s.endPeriod }}节</p>
          <p>已选 {{ s.enrolledCount }}/{{ s.capacity }}</p>
          <el-button type="primary" size="small" :disabled="s.enrolledCount >= s.capacity" @click="enroll(s.scheduleId)">
            {{ s.enrolledCount >= s.capacity ? '已满' : '选课' }}
          </el-button>
        </el-card>
      </el-col>
    </el-row>
    <p v-if="msg">{{ msg }}</p>
  </div>
</template>

<script>
import api from '../api'
export default {
  data() {
    return { schedules: [], msg: '' }
  },
  async mounted() {
    const res = await api.get('/course-schedule')
    this.schedules = res.data
  },
  methods: {
    async enroll(scheduleId) {
      try {
        const res = await api.post('/enrollment/enroll', null, {
          params: { scheduleId }
        })
        const msg = res.data.message || (res.data.success !== false ? '选课成功' : '选课失败')
        if (res.data.success !== false) {
          this.$message.success(msg)
          const r = await api.get('/course-schedule')
          this.schedules = r.data
        } else {
          this.$message.error(msg)
        }
      } catch (e) {
        this.$message.error(e.response?.data?.message || '选课失败')
      }
    }
  }
}
</script>