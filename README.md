库存管理系统（Inventory Management System）

一个基于 Spring Boot 和 Vue 3 的轻量级企业库存管理后台，实现了用户权限控制、商品管理、出入库单、库存盘点和低库存预警。采用前后端分离架构，也支持单端口一体化部署。
一、功能特性
登录认证：登录后签发 JWT Token，由拦截器统一校验，过期自动失效。
权限控制：基于角色的权限模型（RBAC），通过自定义注解声明接口所需权限，拦截器统一拦截。
商品管理：支持按分类、状态、时间范围多条件组合分页查询，并使用联合索引优化。
出入库单：主从表设计，出入库操作通过数据库事务保证一致性，并用乐观锁防止库存超卖。
库存盘点：采用快照法生成盘点单，盘点期间出入库互不影响，支持按差异调账。
低库存预警：库存低于设定阈值的商品自动进入预警清单。
管理后台：使用 Vue 3 加 Element Plus 构建，包含登录页、路由守卫、请求拦截器。
二、技术栈
后端： Java 17、Spring Boot 4、MyBatis、PageHelper 分页插件、MySQL、HikariCP 连接池、JWT（jjwt）。
前端： Vue 3（组合式 API）、Vite、Element Plus、Axios、Vue Router。
三、项目结构
后端主要包： controller 控制器层 service 业务层（事务与乐观锁） mapper MyBatis 接口层 entity 实体类 dto 请求与响应对象 common 统一返回结构、全局异常、权限注解 interceptor JWT 拦截器 config 拦截器注册与跨域配置 utils JWT 工具类
前端工程位于 inventory-ui 目录。
四、数据库表
user 用户表 role 角色表 permission 权限表 user_role 用户角色关联表 role_permission 角色权限关联表 product 商品表（含联合索引） stock_order 出入库单主表 stock_order_item 出入库单明细表 stock_take 盘点单主表 stock_take_item 盘点单明细表
五、快速开始
环境要求：JDK 17 及以上、MySQL 8、Node.js 16 及以上。
第一步，创建数据库 inventory_db，并执行各模块的建表语句。
第二步，修改 src/main/resources/application.yml 中的数据库账号密码。
第三步，启动后端，在项目根目录执行： mvnw.cmd spring-boot:run
第四步，启动前端，进入 inventory-ui 目录执行： npm install npm run dev 然后浏览器访问 http://localhost:5173
六、一体化部署（单端口）
先打包前端：进入 inventory-ui 目录执行 npm run build，把生成的 dist 目录内容复制到后端的 src/main/resources/static 目录。
再打包后端：执行 mvnw.cmd clean package -DskipTests。
最后运行：java -jar target/inventory-system-0.0.1-SNAPSHOT.jar
访问 http://localhost:8080 即可，前后端同端口，无跨域问题。
七、默认账号
管理员：用户名 admin，密码 123456，拥有全部权限。 操作员：用户名 worker，密码 123456，仅有查看与新增权限。
八、技术亮点
联合索引优化：针对商品多条件查询，按最左前缀法则与缓存局部性原理建立 category_id、status、create_time 联合索引，执行计划扫描行数从约一万行降至约三百行。
乐观锁防超卖：扣减库存时附加库存充足的条件判断，并发出库场景下保证库存不会变为负数。
事务一致性：写单据与扣减库存在同一事务中，任一环节失败则全部回滚。
快照法盘点：创建盘点单即冻结当时库存快照，盘点与日常出入库并行不冲突。
注解式鉴权：在接口上标注所需权限码，拦截器统一校验，代码简洁易维护。
九、提交规范
遵循 Conventional Commits 规范，使用 feat、fix、perf、refactor、docs、chore 等前缀。
