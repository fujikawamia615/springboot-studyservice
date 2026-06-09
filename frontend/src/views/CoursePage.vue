<template>
  <div>
    <h2>{{ pageTitle }}</h2>
    <el-row style="margin-bottom:15px">
      <el-button v-if="role === 'admin'" type="primary" @click="add">新增课程</el-button>
      <el-input v-if="role === 'admin'" v-model="keyword" placeholder="搜索课程" style="width:200px;margin-left:10px" clearable @input="search" />
    </el-row>
    <el-table :data="list" border stripe>
      <el-table-column prop="courseId" label="ID" width="80" v-if="role === 'admin'" />
      <el-table-column prop="scheduleId" label="安排ID" width="80" v-if="role === 'teacher'" />
      <el-table-column label="课程名称">
        <template #default="{ row }">{{ row.courseName }}</template>
      </el-table-column>
      <el-table-column label="教师" v-if="role === 'admin'">
        <template #default="{ row }">{{ row.teacherName }}</template>
      </el-table-column>
      <el-table-column label="时间" v-if="role === 'teacher'">
        <template #default="{ row }">周{{ row.dayOfWeek }} 第{{ row.startPeriod }}~{{ row.endPeriod }}节</template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button v-if="role === 'admin'" size="small" @click="edit(row)">编辑</el-button>
          <el-button v-if="role === 'admin'" size="small" type="danger" @click="del(row)">删除</el-button>
          <el-button v-if="role === 'teacher'" size="small" type="primary" @click="viewEnrollments(row.scheduleId)">查看名单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="450px">
      <el-form :model="form">
        <el-form-item label="ID" v-if="!isEdit"><el-input v-model.number="form.courseId" /></el-form-item>
        <el-form-item label="课程名称"><el-input v-model="form.courseName" /></el-form-item>
        <el-form-item label="授课教师">
          <el-select v-model="form.teacherId" style="width:100%">
            <el-option v-for="t in teachers" :key="t.teacherId" :label="t.teacherName" :value="t.teacherId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'
export default {
  data() {
    const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
    return {
      role: (info.role || 'admin').toLowerCase(),
      list: [], keyword: '', teachers: [],
      dialogVisible: false, isEdit: false, form: {}
    }
  },
  computed: {
    pageTitle() { return this.role === 'teacher' ? '我的课程' : '课程管理' }
  },
  async mounted() {
    if (this.role === 'admin') this.teachers = (await api.get('/teacher')).data
    this.fetch()
  },
  methods: {
    async fetch() {
      if (this.role === 'teacher') this.list = (await api.get('/course-schedule')).data
      else this.list = (await api.get('/course')).data
    },
    async search() {
      if (this.role !== 'admin') return
      this.list = (await api.get('/course/search', { params: { courseName: this.keyword || undefined } })).data
    },
    add() { this.isEdit = false; this.form = {}; this.dialogVisible = true },
    edit(row) { this.isEdit = true; this.form = { ...row }; this.dialogVisible = true },
    async save() {
      if (this.isEdit) await api.put('/course', this.form)
      else await api.post('/course', this.form)
      this.dialogVisible = false; this.fetch()
    },
    async del(row) {
      await this.$confirm('确定删除？', '提示', { type: 'warning' })
      await api.delete(`/course/${row.courseId}`)
      this.$message.success('已删除'); this.fetch()
    },
    viewEnrollments(scheduleId) { this.$router.push(`/enrollment-list/${scheduleId}`) }
  }
}
</script>
