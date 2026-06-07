# Contractor Control Platform

工程外协服务商数字化管控平台 MVP，采用 React + Java 8 + Spring Boot 2.7 + SQLite。

## Directory layout

- `frontend`: React + Vite + Ant Design 前端管理台
- `backend`: Java 8 + Spring Boot + Spring Security + JPA + Flyway + SQLite 后端 API

前端共享类型、常量与状态枚举已并入 `frontend/src/shared/index.ts`。

## Requirements

- Node.js
- pnpm 9
- JDK 1.8
- Maven 3.8+

## Quick start

```bash
npx pnpm@9.15.0 install
mvn -f backend/pom.xml test
mvn -f backend/pom.xml spring-boot:run
npx pnpm@9.15.0 dev:web
```

也可以直接使用根脚本：

```bash
npx pnpm@9.15.0 typecheck
npx pnpm@9.15.0 build:web
npx pnpm@9.15.0 build:api
npx pnpm@9.15.0 build
```

前端默认地址：`http://localhost:5173`

后端默认地址：`http://localhost:3001`

## Demo accounts

- `platform_admin / Admin123456`
- `project_admin / Project123456`

## Implemented MVP scope

- 登录与 JWT 鉴权
- 服务商管理
- 项目管理
- 准入申请与审核
- 到期预警与首页统计

## Notes

- 未登录访问业务路由会跳转到登录页。
- 不带 token 访问受保护接口会返回 `401`，body 为 `{ "message": "未授权" }`。
- 当前使用 SQLite，本地数据库文件位于 `backend/data/dev.db`。
- 后端默认通过仓库根目录启动，SQLite 连接会指向 `backend/data/dev.db`，不会在仓库根误生成 `data/dev.db`。
- Flyway 会在启动时自动初始化表结构，应用首次启动时会自动写入 demo 数据。
- Web 构建阶段存在 Ant Design 带来的 chunk size 警告，但不影响当前主流程运行。
