<template>
  <div>
    <h2>我的选课</h2>
    <el-table v-if="enrollments.length" :data="enrollments" border stripe>
      <el-table-column prop="courseName" label="课程" />
      <el-table-column prop="teacherName" label="教师" />
      <el-table-column label="时间">
        <template #default="{ row }">
          周{{ row.dayOfWeek }} 第{{ row.startPeriod }}~{{ row.endPeriod }}节
        </template>
      </el-table-column>
      <el-table-column prop="enrollTime" label="选课时间" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="drop(row.enrollmentId)">退选</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无选课记录" />
  </div>
</template>

<script>
import api from '../api'
export default {
  data() {
    return { enrollments: [] }
  },
  async mounted() {
    const res = await api.get('/enrollment/my')
    this.enrollments = res.data
  },
  methods: {
    async drop(enrollmentId) {
      try {
        const res = await api.post('/enrollment/drop', null, {
          params: { enrollmentId }
        })
        if (res.data.success !== false) {
          this.$message.success(res.data.message || '退选成功')
          const r = await api.get('/enrollment/my')
          this.enrollments = r.data
        } else {
          this.$message.error(res.data.message || '退选失败')
        }
      } catch (e) {
        this.$message.error(e.response?.data?.message || '退选失败')
      }
    }
  }
}
</script>