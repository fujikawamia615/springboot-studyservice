<template>
  <div>
    <el-row style="margin-bottom:15px">
      <el-button type="primary" @click="add">新增学生</el-button>
      <el-input v-model="keyword" placeholder="搜索姓名" style="width:200px;margin-left:10px" clearable @input="search" />
      <el-select v-model="deptFilter" placeholder="学院" clearable style="width:140px;margin-left:10px" @change="search">
        <el-option v-for="d in departments" :key="d.departmentId" :label="d.departmentName" :value="d.departmentId" />
      </el-select>
    </el-row>
    <el-table :data="list" border stripe>
      <el-table-column prop="studentId" label="ID" width="80" />
      <el-table-column prop="studentName" label="姓名" />
      <el-table-column prop="departmentName" label="学院" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学生' : '新增学生'" width="450px">
      <el-form :model="form">
        <el-form-item label="ID" v-if="!isEdit">
          <el-input v-model.number="form.studentId" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.studentName" />
        </el-form-item>
        <el-form-item label="学院">
          <el-select v-model="form.departmentId" style="width:100%">
            <el-option v-for="d in departments" :key="d.departmentId" :label="d.departmentName" :value="d.departmentId" />
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
    return {
      list: [], keyword: '', deptFilter: null,
      departments: [], dialogVisible: false, isEdit: false, form: {}
    }
  },
  async mounted() {
    this.departments = (await api.get('/department')).data
    this.fetch()
  },
  methods: {
    async fetch() {
      this.list = (await api.get('/student/withdept')).data
    },
    async search() {
      const p = {}
      if (this.keyword) p.studentName = this.keyword
      if (this.deptFilter) p.departmentId = this.deptFilter
      this.list = (await api.get('/student/search', { params: p })).data
    },
    add() { this.isEdit = false; this.form = {}; this.dialogVisible = true },
    edit(row) { this.isEdit = true; this.form = { ...row }; this.dialogVisible = true },
    async save() {
      if (this.isEdit) await api.put('/student', this.form)
      else await api.post('/student', this.form)
      this.dialogVisible = false; this.fetch()
    },
    async del(row) {
      await this.$confirm('确定删除？', '提示', { type: 'warning' })
      await api.delete(`/student/${row.studentId}`)
      this.$message.success('已删除'); this.fetch()
    }
  }
}
</script>
