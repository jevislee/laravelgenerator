# laravelgenerator
laravel crud code generator(java spring boot version)

使用方法:

1.下载源代码,修改resources/application.yml里的database配置

2.在项目根目录执行mvn install打包成jar包

3.执行java -Dfile.encoding=utf-8 -Dserver.port=8080 -jar laravelgenerator.jar运行程序,端口号按需要修改

4.访问http://localhost:8080/swagger-ui.html, 可以看到有四个请求

| 请求接口      | 说明           |
| :----------  | -------------- |
| GET /check       | 检查数据库里的表和字段命名是否和mysql关键字冲突等 |
| GET /generate    | 根据表名列表(以逗号分隔)生成laravel框架中的controller,model,route;还增加了生成rest接口测试模版和字段说明markdown模版 |
| GET /tablenames  | 返回数据库中的所有表名(以逗号分隔) |
| GET /testquery   | 执行select查询语句,检查是否会有报错 |
