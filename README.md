# SmartPutty

## 环境说明

jdk 1.8.0_231

PS:

1. pom.xml org.eclipse.swt.win32.win32 根据 jdk 的版本修改依赖包的版本.
2. 使用 OpenJDK\jdk-18.0.2 貌似有问题, 可以启动, 但是调用 Putty 时会闪退

## 目录说明

```
config           应用配置目录
  config.yaml    应用配置文件
  ssh.csv        账号信息, 因为自用 所以未做加密处理, 请自行保护
doc              文档目录
icon             应用图标目录
src              源代码
logs             日志目录
target           编译生成目录
```

## 快捷键

Ctrl+R 打开程序路径配置弹窗
标签栏 左右方向键切换Tab

## 启动脚本

### Start.bat
启动脚本, 因本机安装了多个jdk版本, 对于高版本或者特殊版本, 启动Putty会闪退, 所以需要指定javaw绝对路径
因`C:\Program Files`带空格, 所以使用缩写的形式, 具体路径根据实际情况进行调整
```
start C:\Progra~1\Java\jdk1.8.0_211\bin\javaw.exe -jar SmartPutty.jar
```

### Start.vbs
因使用Start.bat启动时有黑窗, 所以使用vbs处理下