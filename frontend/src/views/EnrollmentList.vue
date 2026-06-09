<template>
  <div>
    <el-button style="margin-bottom:15px" @click="$router.back()">← 返回</el-button>
    <h2>选课名单</h2>
    <el-table :data="list" border stripe>
      <el-table-column prop="studentName" label="学生姓名" />
      <el-table-column prop="enrollTime" label="选课时间" />
    </el-table>
    <el-empty v-if="!list.length" description="暂无学生选课" />
  </div>
</template>

<script>
import api from '../api'
export default {
  data() { return { list: [] } },
  async mounted() {
    const res = await api.get(`/enrollment/schedule/${this.$route.params.scheduleId}`)
    this.list = res.data
  }
}
</script>
