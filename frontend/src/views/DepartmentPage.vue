<template>
  <div>
    <el-row style="margin-bottom:15px">
      <el-button type="primary" @click="add">新增学院</el-button>
      <el-input v-model="keyword" placeholder="搜索名称" style="width:200px;margin-left:10px" clearable @input="search" />
    </el-row>
    <el-table :data="list" border stripe>
      <el-table-column prop="departmentId" label="ID" width="80" />
      <el-table-column prop="departmentName" label="学院名称" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学院' : '新增学院'" width="400px">
      <el-form :model="form">
        <el-form-item label="ID" v-if="!isEdit">
          <el-input v-model.number="form.departmentId" />
        </el-form-item>
        <el-form-item label="学院名称">
          <el-input v-model="form.departmentName" />
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
    return { list: [], keyword: '', dialogVisible: false, isEdit: false, form: {} }
  },
  mounted() { this.fetch() },
  methods: {
    async fetch() {
      this.list = (await api.get('/department')).data
    },
    async search() {
      const p = { departmentName: this.keyword || undefined }
      this.list = (await api.get('/department/search', { params: p })).data
    },
    add() { this.isEdit = false; this.form = {}; this.dialogVisible = true },
    edit(row) { this.isEdit = true; this.form = { ...row }; this.dialogVisible = true },
    async save() {
      if (this.isEdit) await api.put('/department', this.form)
      else await api.post('/department', this.form)
      this.dialogVisible = false; this.fetch()
    },
    async del(row) {
      await this.$confirm('确定删除？', '提示', { type: 'warning' })
      await api.delete(`/department/${row.departmentId}`)
      this.$message.success('已删除'); this.fetch()
    }
  }
}
</script>
