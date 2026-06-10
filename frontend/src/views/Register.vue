<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;background:#f5f7fa">
    <el-card style="width:420px">
      <h2 style="text-align:center;margin-bottom:24px">注册账号</h2>
      <el-form :model="form" @keyup.enter="register">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio value="student">学生</el-radio>
            <el-radio value="teacher">教师</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="form.role === 'teacher' ? '教师ID' : '学生ID'">
          <el-input v-model.number="form.referenceId" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="register">注 册</el-button>
        </el-form-item>
      </el-form>
      <p v-if="error" style="color:#f56c6c;text-align:center">{{ error }}</p>
      <p v-if="success" style="color:#67c23a;text-align:center">{{ success }}</p>
      <p style="text-align:center;margin-top:10px">
        已有账号？<router-link to="/login">去登录</router-link>
      </p>
    </el-card>
  </div>
</template>

<script>
import api from '../api'
export default {
  data() {
    return {
      form: { username: '', password: '', role: 'student', referenceId: null },
      error: '', success: '', loading: false
    }
  },
  methods: {
    async register() {
      this.error = ''; this.success = ''
      if (!this.form.username || !this.form.password) {
        this.error = '请填写用户名和密码'; return
      }
      if (!this.form.referenceId) {
        this.error = '请填写对应的学生/教师ID'; return
      }
      this.loading = true
      try {
        await api.post('/user/register', this.form)
        this.success = '注册成功，即将跳转登录页...'
        setTimeout(() => this.$router.push('/login'), 1500)
      } catch (e) {
        this.error = e.response?.data?.message || '注册失败'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
