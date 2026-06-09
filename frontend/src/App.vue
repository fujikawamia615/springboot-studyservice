<template>
  <el-container v-if="token" style="height:100vh">
    <el-aside width="200px" style="background:#304156">
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <!-- 管理员专属 -->
        <template v-if="role === 'admin'">
          <el-menu-item index="/departments">学院管理</el-menu-item>
          <el-menu-item index="/students">学生管理</el-menu-item>
          <el-menu-item index="/teachers">教师管理</el-menu-item>
          <el-menu-item index="/course-list">课程管理</el-menu-item>
          <el-menu-item index="/course-schedules">课程安排</el-menu-item>
          <el-menu-item index="/enrollment-manage">选课管理</el-menu-item>
          <el-menu-item index="/grades">成绩管理</el-menu-item>
        </template>
        <!-- 教师专属 -->
        <template v-if="role === 'teacher'">
          <el-menu-item index="/course-list">我的课程</el-menu-item>
          <el-menu-item index="/grades">成绩管理</el-menu-item>
        </template>
        <!-- 学生专属 -->
        <template v-if="role === 'student'">
          <el-menu-item index="/courses">可选课程</el-menu-item>
          <el-menu-item index="/my-enrollments">我的选课</el-menu-item>
          <el-menu-item index="/grades">我的成绩</el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #dcdfe6;display:flex;align-items:center;justify-content:space-between">
        <h2 style="margin:0">教务管理系统</h2>
        <span v-if="userInfo">{{ userInfo.username }} ({{ roleText }})</span>
        <el-button size="small" @click="logout">退出</el-button>
      </el-header>
      <el-main style="background:#f5f7fa">
        <router-view :key="$route.fullPath" />
      </el-main>
    </el-container>
  </el-container>
  <router-view v-else />
</template>

<script>
export default {
  data() {
    return {
      token: localStorage.getItem('token'),
      userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
    }
  },
  computed: {
    role() {
      const r = (this.userInfo?.role || '').toLowerCase()
      return r || 'admin'
    },
    roleText() {
      const r = this.role
      if (r === 'admin') return '管理员'
      if (r === 'teacher') return '教师'
      if (r === 'student') return '学生'
      return r
    }
  },
  watch: {
    '$route'() {
      this.token = localStorage.getItem('token')
      this.userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      this.token = null
      this.$router.push('/login')
    }
  }
}
</script>

<style>
body { margin: 0; }
</style>