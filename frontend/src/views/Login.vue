<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;background:#f5f7fa">
    <el-card style="width:380px">
      <h2 style="text-align:center;margin-bottom:24px">教务管理系统</h2>
      <el-form :model="form" @keyup.enter="login">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="login">登 录</el-button>
        </el-form-item>
      </el-form>
      <p v-if="error" style="color:#f56c6c;text-align:center">{{ error }}</p>
      <p style="text-align:center;margin-top:10px">
        没有账号？<router-link to="/register">去注册</router-link>
      </p>
    </el-card>
  </div>
</template>

<script>
import api from '../api'
export default {
  data() {
    return { form: { username: '', password: '' }, error: '', loading: false }
  },
  methods: {
    async login() {
      this.error = ''
      this.loading = true
      try {
        localStorage.removeItem('token')
        const res = await api.post('/user/login', this.form)
        // 后端异常处理器返回了 success:false，但状态码是 200，需要手动拦截
        if (res.data.success === false) {
          this.error = res.data.message || '登录失败'
          return
        }
        const token = res.data.token
        if (!token) {
          this.error = '登录失败：未获取到令牌'
          return
        }
        localStorage.setItem('token', token)
        // 从 JWT 解析用户信息（payload 是 base64）
        const payload = JSON.parse(atob(token.split('.')[1]))
        localStorage.setItem('userInfo', JSON.stringify({
          userId: payload.sub,
          username: this.form.username,
          role: payload.role,
          referenceId: payload.referenceId
        }))
        this.$router.push('/departments')
      } catch (e) {
        const msg = e.response?.data?.message
                 || e.response?.data?.error
                 || e.message
                 || '用户名或密码错误'
        this.error = msg
      } finally {
        this.loading = false
      }
    }
  }
}
</script>