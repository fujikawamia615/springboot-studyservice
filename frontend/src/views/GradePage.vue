<template>
  <div>
    <h2>{{ pageTitle }}</h2>
    <!-- 教师：先选课程安排 -->
    <el-row v-if="role === 'teacher'" style="margin-bottom:15px">
      <el-select v-model="selectedSchedule" placeholder="选择课程" style="width:300px" @change="loadGrades">
        <el-option v-for="s in schedules" :key="s.scheduleId" :label="s.courseName + ' (周' + s.dayOfWeek + ' 第' + s.startPeriod + '~' + s.endPeriod + '节)'" :value="s.scheduleId" />
      </el-select>
      <el-button type="primary" style="margin-left:10px" @click="add">新增成绩</el-button>
    </el-row>
    <!-- 管理员：直接显示全部 -->
    <el-row v-if="role === 'admin'" style="margin-bottom:15px">
      <el-button type="primary" @click="add">新增成绩</el-button>
    </el-row>
    <el-table :data="list" border stripe>
      <el-table-column v-if="role !== 'student'" prop="gradeId" label="ID" width="80" />
      <el-table-column v-if="role === 'student'" prop="courseName" label="课程" />
      <el-table-column v-if="role !== 'student'" prop="studentId" label="学生ID" />
      <el-table-column v-if="role !== 'student'" prop="scheduleId" label="课程安排ID" />
      <el-table-column prop="grade" label="成绩" />
      <el-table-column v-if="role !== 'student'" label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageSize && role === 'admin'"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      layout="prev, pager, next"
      style="margin-top:15px"
      @current-change="changePage"
    />
    <el-empty v-if="!list.length" description="暂无成绩记录" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑成绩' : '新增成绩'" width="450px">
      <el-form :model="form">
        <el-form-item label="学生ID">
          <el-input v-model.number="form.studentId" />
        </el-form-item>
        <el-form-item label="课程安排ID">
          <el-input v-model.number="form.scheduleId" />
        </el-form-item>
        <el-form-item label="成绩">
          <el-input v-model.number="form.grade" />
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
      list: [], dialogVisible: false, isEdit: false, form: {},
      pageNum: 1, pageSize: 10, total: 0,
      schedules: [], selectedSchedule: null
    }
  },
  computed: {
    pageTitle() {
      if (this.role === 'student') return '我的成绩'
      return '成绩管理'
    }
  },
  mounted() { this.fetch() },
  methods: {
    async fetch() {
      if (this.role === 'student') {
        const res = await api.get('/grade/my')
        this.list = res.data
        this.total = this.list.length
      } else if (this.role === 'teacher') {
        this.schedules = (await api.get('/course-schedule')).data
        if (this.schedules.length) {
          this.selectedSchedule = this.schedules[0].scheduleId
          this.loadGrades()
        }
      } else {
        const res = await api.get('/grade', { params: { page: this.pageNum, size: this.pageSize } })
        this.list = res.data.list || res.data
        this.total = res.data.total || this.list.length
      }
    },
    async loadGrades() {
      if (!this.selectedSchedule) return
      const res = await api.get(`/grade/schedule/${this.selectedSchedule}`)
      this.list = res.data
      this.total = this.list.length
    },
    changePage(p) { this.pageNum = p; this.fetch() },
    add() { this.isEdit = false; this.form = {}; this.dialogVisible = true },
    edit(row) { this.isEdit = true; this.form = { ...row }; this.dialogVisible = true },
    async save() {
      if (this.isEdit) await api.put('/grade', this.form)
      else await api.post('/grade', this.form)
      this.dialogVisible = false; this.fetch()
    },
    async del(row) {
      await this.$confirm('确定删除？', '提示', { type: 'warning' })
      await api.delete(`/grade/${row.gradeId}`)
      this.$message.success('已删除'); this.fetch()
    }
  }
}
</script>
