# Imooc Bilibili 模仿后端项目

# 项目结构

**业务结构**

顶层：用户服务（注册登录、会员权限、~~查找视频~~，等等）

中间：视频观看，实时弹幕

底层：管理后台（视频上传、~~数据统计~~、消息推送，等等）

**技术架构**

IntelliJ IDEA、Maven

JDK 11、Spring Boot、Lombok、MySQL、MyBatis、Redis、RocketMQ

**开发模式**

单体应用，经典 MVC 模式

RESTful 接口

* 每一个 URI 代表一种资源；
* 通过 GET（查）、POST（增）、PUT（改）、DELETE（删）等 HTTP 方法管理资源；
* POST 与 PUT 都能创建资源，但 PUT 需要满足幂等性；
* 尽量使用名词复数描述资源，小写形式，多个单词使用 "-" 连接。

# 通用配置

**数据加密**

*com.imooc.bilibili.assistant.util.AESUtil*：对称加密，未使用到。

*com.imooc.bilibili.assistant.util.MD5Util*：单向加密，存库前处理数据，不直接存原文。

*com.imooc.bilibili.assistant.util.RSAUtil*：非对称加密，传输密码等信息。

**结果对象**

*com.pochaiyi.assistant.domain.JsonResponse*

统一响应格式，便于接口处返回结果的编写。

**JSON 信息转换**

*com.imooc.bilibili.assistant.config.JsonHttpMessageConverterConfig#fastJsonHttpMessageConverters*

使用 FastJson 转换所有响应为 JSON 格式

**全局异常处理**

*com.imooc.bilibili.assistant.exception.CommonGlobalExceptionHandler*

捕获所有异常，返回友好的信息。

# 用户功能

**用户登录**

基于 JWT 的 Token 用户授权

**粉丝关注**

部分方法有点复杂，难点在于各种数据的组织。

**动态提醒**

发布动态 -> 持久化然后生产消息 -> PUSH 消费者收到动态，将其插入粉丝对应的缓存 List（Redis）。

该处使用 Redis List 缓存动态，用户获取动态的操作是 pop，因此动态看到即销毁，但 MySQL 仍有数据。

**权限控制**

基于角色的权限控制（Role-Besed Acess Control，RBAC），权限被分为前端和后端，前端权限控制页面或页面元素的访问，后端权限控制接口或数据的访问。

前端权限，没有难点，连表查询数据库的各种权限，用到 MyBatis 结果映射。

接口权限，基于 AOP 实现，示例 *ApiLimitedRoleAspect* 限制用户的动态发布。

数据权限，同样使用切面编程，示例 *DataLimitedRoleAspect* 限制用户发布动态的类型。

补充代码，用户注册时为其设置默认的角色。

**登录优化**

双 Token 机制，基于上面 JWT 机制，增加一个 RefreshToken。

登陆时保存 RefreshToken，每次验证 JWT 同时检查 RefreshToken，用户退出时删除 RefreshToken。

RefreshToken 有效时间很长，每当 AccessToken 失效，前端便会隐式访问 AccessToken 刷新接口，整个过程对用户完全透明，从而避免反复登录，提升用户体验。

# 视频功能

## 存储服务

项目使用专门的文件服务 FastDFS 存储视频等数据，数据库则更适合结构化数据。

<img src="https://pochaiyi-images.oss-cn-chengdu.aliyuncs.com/imooc-bilibili-c5d463f5.png" style="zoom: 50%;" />

**安装 FastDFS**

拉取镜像

```
docker pull delron/fastdfs:latest
```

创建 Tracker 容器

```
docker run -d --name tracker --network=host \
-v /data/docker/fastdfs/tracker:/var/fdfs \
-v /etc/localtime:/etc/localtime \
delron/fastdfs:latest \
tracker
```

创建 Storage 容器，其中已经预设 nginx 服务。

```
docker run -d --name storage --network=host \
-e TRACKER_SERVER=192.168.200.101:22122 \
-v /data/docker/fastdfs/storage:/var/fdfs \
-v /etc/localtime:/etc/localtime \
delron/fastdfs:latest \
storage
```

**测试 FastDFS**

进入 storage 容器，创建一个文件。

```
echo Hello World,Imooc Bilibili.>/var/fdfs/test.txt
```

存储文件，返回文件 ID，例如 *group1/M00/00/00/wKjIZWc6H0yADtT6AAAAHGFqae8461.txt*。

```
/usr/bin/fdfs_upload_file /etc/fdfs/client.conf /var/fdfs/test.txt
```

尝试根据文件 ID 访问测试文件，成功则安装完成。

```
http://192.168.200.101:8888/group1/M00/00/00/wKjIZWc6H0yADtT6AAAAHGFqae8461.txt
```

## 视频播放

**文件处理**

普通上传、文件删除

断点续传（FDFS 支持），前端把大文件拆成多个片段，然后逐个上传。如此若因网络等问题分片上传失败，只需重新上传这个分片，不会影响其它分片的上传，解决大文件上传困难的问题。

秒传功能，文件上传完毕后，其内容 MD5 加密结果会被保存到数据库。存储文件时需要提供 MD5 加密，服务端查询数据库，如果有记录，说明 FDFS 已有这个文件，直接返回文件的存储路径。

**视频接口**

视频投稿、视频标签、分区分页查询视频、视频点赞、视频收藏、收藏分组、视频投币、视频详情

视频播放，分片下载，请求头字段 range 指定数据范围，服务端通过 Response 分片传输视频给客户端。

视频评论，数据库使用一个邻接表存储评论树，分页查询有点复杂。

另外，需要修改 Spring Boot 配置，增加文件上传大小限制。

## 弹幕推送

首先建立 WebSocket 长连接，客户端发送弹幕，服务端异步缓存和持久化，然后同步发送队列消息。消费者收到弹幕消息，根据 sessionId 得到 WebSocket 用来发送弹幕给客户端。

定时服务，每隔几秒，服务端便会通过 WebSocket 推送当前在线人数的信息。

<img src="https://pochaiyi-images.oss-cn-chengdu.aliyuncs.com/imooc-bilibili-c7f465cb.png" style="zoom:50%;" />
