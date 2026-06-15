import { createRouter, createWebHistory } from 'vue-router'
import Login from './views/Login.vue'
import Register from './views/Register.vue'
import DepartmentPage from './views/DepartmentPage.vue'
import StudentPage from './views/StudentPage.vue'
import TeacherPage from './views/TeacherPage.vue'
import CoursePage from './views/CoursePage.vue'
import CourseSchedulePage from './views/CourseSchedulePage.vue'
import EnrollmentManagePage from './views/EnrollmentManagePage.vue'
import GradePage from './views/GradePage.vue'
import Courses from './views/Courses.vue'
import MyEnrollments from './views/MyEnrollments.vue'
import EnrollmentList from './views/EnrollmentList.vue'

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    // 管理员管理页
    { path: '/departments', component: DepartmentPage, meta: { roles: ['admin'] } },
    { path: '/students', component: StudentPage, meta: { roles: ['admin'] } },
    { path: '/teachers', component: TeacherPage, meta: { roles: ['admin'] } },
    { path: '/course-schedules', component: CourseSchedulePage, meta: { roles: ['admin'] } },
    { path: '/enrollment-manage', component: EnrollmentManagePage, meta: { roles: ['admin'] } },
    { path: '/course-list', component: CoursePage, meta: { roles: ['admin', 'teacher'] } },
    { path: '/grades', component: GradePage, meta: { roles: ['admin', 'teacher', 'student'] } },
    // 学生页
    { path: '/courses', component: Courses, meta: { roles: ['student'] } },
    { path: '/my-enrollments', component: MyEnrollments, meta: { roles: ['student'] } },
    { path: '/enrollment-list/:scheduleId', component: EnrollmentList, meta: { roles: ['admin', 'teacher'] } }
]

const router = createRouter({ history: createWebHistory(), routes })

function getRole() {
    try {
        const info = JSON.parse(localStorage.getItem('userInfo') || 'null')
        return (info?.role || '').toLowerCase() || null
    } catch { return null }
}

const PUBLIC_PATHS = ['/login', '/register']

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    // 已登录用户访问登录/注册页 → 跳转到对应首页
    if (token && PUBLIC_PATHS.includes(to.path)) {
        const role = getRole()
        if (role === 'student') return next('/courses')
        if (role === 'teacher') return next('/course-list')
        return next('/departments')  // admin
    }

    // 未登录访问公开页面 → 放行
    if (PUBLIC_PATHS.includes(to.path)) return next()

    // 未登录 → 登录页
    if (!token) return next('/login')

    // 检查角色权限
    const role = getRole()
    const allowed = to.meta.roles
    if (allowed && !allowed.includes(role)) {
        if (role === 'student') return next('/courses')
        if (role === 'teacher') return next('/course-list')
        return next('/departments')
    }
    next()
})

export default router