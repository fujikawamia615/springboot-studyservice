# 权限记录
## 角色说明

| 角色 | 描述 |
|------|------|
| `ADMIN` | 管理员，拥有全部权限 |
| `teacher` | 教师，管理自己教的课程和学生成绩 |
| `student` | 学生，选课、查看自己的课程表和成绩 |

---

## 权限明细

### 全局规则

- 所有 API 请求必须携带 `Authorization: Bearer <token>` 头（白名单除外）
- `referenceId` 从 JWT 中提取，由 Gateway 透传至下游服务，前端无需传递
- 服务层对教师和学生的数据访问做**行级安全校验**（如教师只能操作自己教的课）

### 白名单（无需登录）

| API | 说明 |
|-----|------|
| `POST /api/user/login` | 用户登录 |
| `POST /api/user/register` | 用户注册 |
| `/swagger-ui/**` | Swagger 文档 |
| `/v3/api-docs/**` | OpenAPI 文档 |

### 学生权限

| API | 方法 | 说明 | 控制层级 |
|-----|:----:|------|:--------:|
| `GET /api/course-schedule` | GET | 查看所有可选课程 | Gateway |
| `POST /api/enrollment/enroll` | POST | 选课（携带 `scheduleId`） | Gateway |
| `POST /api/enrollment/drop` | POST | 退选（携带 `enrollmentId`） | Gateway |
| `GET /api/enrollment/my` | GET | 查看自己的选课列表（从 JWT 取 referenceId） | Gateway + 服务层 |
| `GET /api/student/{id}` | GET | 查看个人信息（仅限自己，服务层校验） | Gateway + 服务层 |
| `GET /api/grade/my` | GET | 查看自己的成绩（未来实现） | — |

**禁止操作：**
- 所有 `POST / PUT / DELETE` 接口（选课/退选除外）
- 访问其他学生的信息
- 教师/管理员相关的接口
- `/api/user/**` 用户管理接口

### 教师权限

| API | 方法 | 说明 | 控制层级 |
|-----|:----:|------|:--------:|
| `GET /api/course-schedule` | GET | 查看**自己教的**课程安排（服务层按 teacher_id 过滤） | Gateway + 服务层 |
| `GET /api/enrollment/schedule/{scheduleId}` | GET | 查看某门课的选课名单（仅限自己教的课） | Gateway + 服务层 |
| `GET /api/teacher/{id}` | GET | 查看个人信息（仅限自己） | Gateway + 服务层 |
| `GET /api/grade/**` | GET | 查询成绩（未来实现） | — |
| `POST / PUT /api/grade/**` | POST/PUT | 录入/修改成绩（仅限自己教的课，未来实现） | — |

**禁止操作：**
- 选课、退选
- 查看其他教师的课程安排
- 学生信息、用户管理
- 课程/课程安排的增删改

### 管理员权限

| API | 说明 |
|-----|------|
| 全部 API | 无限制访问 |

---

## Gateway 过滤规则（`JwtAuthGatewayFilter.java`）

```
请求
  │
  ├─ 白名单路径？ ──→ 直接放行
  │
  ├─ 非 /api/ 路径？ ──→ 直接放行
  │
  └─ 需要验证
       │
       ├─ Token 无效或缺失 ──→ 401 Unauthorized
       │
       └─ Token 有效
            │
            ├─ role = ADMIN ──→ 放行
            │
            ├─ role = student
            │    ├─ GET /api/enrollment/my ──→ 放行
            │    ├─ POST /api/enrollment/enroll ──→ 放行
            │    ├─ POST /api/enrollment/drop ──→ 放行
            │    ├─ 其他非 GET 请求 ──→ 403 Forbidden
            │    └─ 其他 GET 请求 ──→ 放行
            │
            └─ role = teacher
                 ├─ GET /api/enrollment/schedule/* ──→ 放行
                 ├─ GET /api/teacher/* ──→ 放行
                 ├─ GET /api/grade* ──→ 放行
                 ├─ POST/PUT /api/grade* ──→ 放行
                 ├─ 其他非 GET 请求 ──→ 403 Forbidden
                 └─ 其他 GET 请求 ──→ 放行
```

---

## 服务层行级安全校验

Gateway 只做粗粒度的角色校验，精细的数据归属校验在服务层完成：

| 场景 | 校验逻辑 |
|------|---------|
| 教师查看课程安排 | `CourseScheduleService` 只返回 `teacher_id = referenceId` 的记录 |
| 教师查看选课名单 | `EnrollmentController` 先校验 `schedule_id` 对应课程的 `teacher_id == referenceId` |
| 教师录入成绩 | 校验该成绩所属课程的 `teacher_id == referenceId` |
| 学生查看个人信息 | `StudentController` 校验路径 `id == referenceId` |
| 学生查看成绩 | 只返回该学生的成绩记录 |
| 学生查看选课 | 只返回 `student_id = referenceId` 的记录 |

---

## JWT 结构

```json
{
  "sub": "userId",
  "role": "student | teacher | ADMIN",
  "referenceId": "对应 t_student/t_teacher 的主键 id",
  "iat": "...",
  "exp": "..."
}
```

- `sub`（subject）：`user-service` 中 `t_user` 的 `user_id`
- `referenceId`：关联到 `studysys` 中 `t_student.id` 或 `t_teacher.id`

---

## 请求头传递（Gateway → 下游服务）

| 请求头 | 来源 | 说明 |
|--------|------|------|
| `userId` | JWT `sub` | 用户 ID |
| `role` | JWT `role` | 用户角色 |
| `referenceId` | JWT `referenceId` | 关联业务主键 |
