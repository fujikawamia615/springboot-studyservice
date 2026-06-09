<template>
  <div>
    <h2>课程安排管理</h2>
    <el-row style="margin-bottom:15px">
      <el-button type="primary" @click="add">新增安排</el-button>
    </el-row>
    <el-table :data="list" border stripe>
      <el-table-column prop="scheduleId" label="ID" width="80" />
      <el-table-column prop="courseName" label="课程" />
      <el-table-column prop="teacherName" label="教师" />
      <el-table-column label="时间">
        <template #default="{ row }">周{{ row.dayOfWeek }} 第{{ row.startPeriod }}~{{ row.endPeriod }}节</template>
      </el-table-column>
      <el-table-column prop="capacity" label="容量" width="80" />
      <el-table-column prop="enrolledCount" label="已选" width="80" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑安排' : '新增安排'" width="500px">
      <el-form :model="form">
        <el-form-item label="课程">
          <el-select v-model="form.courseId" style="width:100%">
            <el-option v-for="c in courses" :key="c.courseId" :label="c.courseName" :value="c.courseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="form.teacherId" style="width:100%">
            <el-option v-for="t in teachers" :key="t.teacherId" :label="t.teacherName" :value="t.teacherId" />
          </el-select>
        </el-form-item>
        <el-form-item label="星期">
          <el-select v-model="form.dayOfWeek" style="width:100%">
            <el-option :value="1" label="周一" />
            <el-option :value="2" label="周二" />
            <el-option :value="3" label="周三" />
            <el-option :value="4" label="周四" />
            <el-option :value="5" label="周五" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始节次">
          <el-input-number v-model="form.startPeriod" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="结束节次">
          <el-input-number v-model="form.endPeriod" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="form.capacity" :min="1" :max="200" />
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
    return {
      list: [], courses: [], teachers: [],
      dialogVisible: false, isEdit: false, form: {}
    }
  },
  async mounted() {
    this.courses = (await api.get('/course')).data
    this.teachers = (await api.get('/teacher')).data
    this.fetch()
  },
  methods: {
    async fetch() {
      this.list = (await api.get('/course-schedule')).data
    },
    add() { this.isEdit = false; this.form = { dayOfWeek: 1, startPeriod: 1, endPeriod: 2, capacity: 60 }; this.dialogVisible = true },
    edit(row) { this.isEdit = true; this.form = { ...row }; this.dialogVisible = true },
    async save() {
      if (this.isEdit) await api.put('/course-schedule', this.form)
      else await api.post('/course-schedule', this.form)
      this.dialogVisible = false; this.fetch()
    },
    async del(row) {
      await this.$confirm('确定删除？', '提示', { type: 'warning' })
      await api.delete(`/course-schedule/${row.scheduleId}`)
      this.$message.success('已删除'); this.fetch()
    }
  }
}
</script>
