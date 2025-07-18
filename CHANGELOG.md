# Changelog

## 25.717.1001

1. 设置选中标签的背景色为苹果绿, 显眼点

## 25.709.1100

1. 换机子了, 分辨率高, 调整下样式

## 25.416.1558

1. session列表框支持排序

## 25.328.1128

1. 删除Quick Connect中手动连接的输入框按钮等
2. 添加笔记功能, 根据当前tab标题存储, 支持记录窗口位置

## 25.327.1734

1. 在Quick Connect添加快捷搜索下拉框, 选中后回车可直接连接
2. 在tab右键菜单 添加Edit Session可修改会话信息, 修改保存时自动以新信息创建窗口

## 24.1128.1328

1. 删除较少使用的快捷方式: vnc, notepad, capture, calclator, remote_desk等
2. 有需要的话可以在配置文件中添加快捷方式
3. 修改版本号规则, 直接按日期

## 1.3.0 - 2024-07-24

- hutool -> 5.8.29
- lombok -> 1.18.34
- 修改session保存逻辑, 用户不同则新增, 相同则替换

## 1.2.9 - 2024-05-10

- hutool -> 5.8.27
- lombok -> 1.18.32
- 控制台输出修改编码为GBK
- 设置版本号 每次更新直接修改版本号吧
- 删除无用功能

## 1.2.8 - 2023-10-13

- hutool -> 5.8.16
- lombok -> 1.18.30
- snakeyaml -> 2.2
- snakeyaml需要自实现TagInspector, 原TrustedPrefixesTagInspector因安全问题 已删除

## 1.2.7 - 2023-03-24

- 修改Putty全屏无标题边框
- 修改Mintty的标签名和错误提示信息
- 调整Kill All同时kill Putty和Mintty

## 1.2.6 - 2023-03-23

- hutool -> 5.8.15
- snakeyaml -> 2.0 需要设置tagInspector, 默认不支持globle tag
- Clipboard菜单项选中时, 弹个tip显示下具体命令信息

## 1.2.5 - 2023-02-16

- hutool -> 5.8.12 之前版本爆出漏洞 https://gitee.com/dromara/hutool/issues/I6AEX2
- lombok -> 1.18.26
- 修改启动脚本即README说明

## 1.2.4 - 2022-07-08

- hutool -> 5.8.4
- 调整程序调用方式
- 调整默认配置路径

## 1.2.3 - 2022-06-24

- 设置打开对话框显示位置
- 调整 closeOtherTabs 倒序关闭

## 1.2.2 - 2022-06-13

- logback -> 1.2.11
- hutool -> 5.8.3
- lombok -> 1.18.24
- 打开会话对话框 添加上移 下移按钮, 用于调整顺序
- 调整部分配置项类型
- p3c 校验
- 添加 clipboard 菜单, 记录一些常用的命令, 选中后 复制到剪贴板

## 1.2.1 - 2021-01-20

- hutool -> 5.7.20
- 合并配置文件, 使用同一个, 调整使用 yaml 格式
- 调整打开会话弹框, 隐藏 session 列

## 1.1.27 - 2021-11-09

- hutool -> 5.7.16
- double click close add confirm dialog

## 1.1.26 - 2021-10-25

- hutool -> 5.7.15
- lombok -> 1.18.22
- fix typo

## 1.1.25 - 2021-09-22

- hutool -> 5.7.13
- logback -> 1.2.6
- 添加 mintty 会话时, 不校验 host 等
- 调整会话编辑框中, host 和 intranet 下拉框的列表获取

## 1.1.24 - 2021-07-16

- 修复一些代码检测问题

## 1.1.23 - 2021-05-18

- 删除重复类型的依赖 jar, 保留 hutool, 减少程序包体积
- 调整 tabName 规则
- 调整 logback 配置

## 1.1.22 - 2021-05-11

- pom 依赖版本更新
- 修改配置文件 get/set 方法
- 调整使用 csv 文件 记录账号信息, 方便修改

## 1.1.21 - 2021-04-29

- 修改 hutool 版本为 5.6.4
- 添加关闭其他标签, 关闭所有标签, 重载所有标签

## 1.1.20 - 2021-01-08

- 修改 hutool 版本为 5.5.7
- 添加 guava 依赖 版本 29.0-jre
- 提取配置常量
- tab 右键菜单 添加拷贝标签名

## 1.1.18 - 2020-12-10

- 修改 hutool 版本为 5.5.2

## 1.1.17 - 2020-10-13

- 修改 hutool 版本为 5.4.4
- 添加 maven 插件 git-commit-id-plugin

### Changed

## 1.1.16 - 2020-09-09

### Changed

- 修改 hutool 版本为 5.4.1
- 代码规范调整
- 配置文件调整试用 hutool 的 Setting
- 升级依赖 jar 包版本
- 调整会话查询问题, 添加端口查询条件

## 1.1.15 - 2020-06-24

### Changed

- 修改 hutool 版本为 5.3.7
- 新增界面 添加默认端口 22

## 1.1.14 - 2020-04-17

### Changed

- 修改 hutool 版本为 5.3.0

## 1.1.13 - 2020-03-31

### Changed

- 修改 hutool 版本为 5.2.5
- 会话查询按 name 排序

## 1.1.12 - 2020-03-06

### Changed

- 修改 hutool 版本为 5.2.0
- 会话编辑界面 增加展示端口编辑

## 1.1.11 - 2019-12-31

### Changed

- 调整包路径和命名规范
- 修改 assembly.xml 打包配置
- 修改 logback 日志打印配置
- 修改 hutool 版本号为 5.1.0
- 程序路径配置直接修改编辑框地址 无法保存 bug 修改

---

## 1.1.10 - 2019-10-18

### Changed

- 修改 hsqldb 版本为 2.5.0, 升级后表字段 user 貌似查询有问题, 修改字段名为 username
- 修改 hutool 版本为 5.0.1

---

## 1.1.9 - 2019-10-08

### Changed

- pom 依赖版本修改
- 修改 log 日志保留天数为 2 天
- README.MD 描述修改

### Fixed

- 单词拼写检查 修复

---

## 1.1.8 2019/07/04

### 新特性

### Bug 修复

- jar 包版本调整
- 调整使用 try with resources
- 调整包结构
- 缺陷修复

---

## 1.1.7 2019/06/24

### 新特性

- 添加标签页拖动功能

### Bug 修复

- 修改代码支持 jdk32 位和 64 位
- 调整枚举包路径
- 提取公共常量
- 提取 pom jar 包版本

---

## 1.1.6 2018/10/19

1. 修改日志文件名
2. 代码优化
3. 修改 About 菜单为 Help 菜单
4. 添加 lombok 依赖

---

## 1.1.5 2018/7/18

1. 修改标签页 标题
2. 修复切换 tab 时, tab 不存在异常退出问题

---

## 1.1.4 2018/7/18

1. 修改 OpenSessionDialog 字段宽度

---

## 1.1.3 2018/6/23

1. 调整配置支持 64 位机器的焦点切换

---

## 1.1.2 2018/3/22

1. 修改使用 logback
2. 升级部分 jar 包版本
3. 启动滚动条 边框调整

---

## 1.1.1 2018/3/20

1. 修改包名, 大写改小写
2. 调整 VERSION 的配置方式, 删除 VERSION 文件

---

## 1.1.0 2018/1/30

1. 调整支持 KiTTY

---

## 1.0.9 2018/1/23

1. File 去除 Remote Desktop 菜单

---

## 1.0.8 2018/1/4

1. 注释 Capture 菜单

---

## 1.0.7 2018/1/2

1. putty load 配置时 添加判空

---

## 1.0.6 2017/11/25

1. 添加名称信息
2. log4j 日志打印调整

---

## 1.0.5 2017/11/24

1. 修改程序路径配置实现, 实现动态添加

---

## 1.0.4 2017/11/23

1. 删除无用的右键菜单
2. mintty 默认打开~目录

---

## 1.0.3 2017/11/23

1. 修复新增,修改 不是保存 putty session 问题
2. 调整标签页标题

---

## 1.0.2

1. 快速连接栏 添加 host 编辑框
2. 新增窗口 增加 putty session 选择框

---

## 1.0.1

1. 添加支持 cygwin 的 mintty 程序
2. 添加显示密码功能
3. 修改原有程序程序路径设置 bug
