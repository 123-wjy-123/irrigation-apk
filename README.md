# 精准灌溉 App（Android，WebView 壳）

全屏 WebView 打开树莓派上的网页。默认地址 `http://10.42.0.1:8080`（Pi 热点），
**长按顶部细条**可改成 `http://192.168.1.127:8080` 之类，改完会记住。

- 包名 `com.irrigation.web`，最低 Android 7.0
- 允许局域网 http 明文
- 返回键先网页后退，退到头才退出

## 一、在 GitHub 上出 APK（不用装 Android Studio）

### 1. 建一个空仓库

浏览器打开 <https://github.com/new>：

- Repository name：`irrigation-apk`（随便）
- **不要**勾选 "Add a README file"（否则推送会冲突）
- Private 或 Public 都行（Private 也能用 Actions，免费额度够）

### 2. 推这个目录上去

在本目录（`产出文档/12-安卓App`）里：

```bash
git init -b main
git add .
git commit -m "精准灌溉 App"
git remote add origin https://github.com/你的用户名/irrigation-apk.git
git push -u origin main
```

第一次推送会弹出 GitHub 登录（浏览器授权），登录一次即可。
如果要求密码，用 **Personal Access Token**（GitHub → Settings → Developer settings →
Personal access tokens → Generate new token，勾 `repo`），把 token 当密码输入。

### 3. 让它编译

推送后 GitHub 会自动开始构建：

1. 打开仓库 → 顶部 **Actions**；
2. 左侧选 **Build APK**，能看到一次运行（黄点＝进行中，绿勾＝成功，约 2–3 分钟）；
3. 点进那次运行，页面底部 **Artifacts** → 下载 **irrigation-apk.zip**；
4. 解压得到 `app-debug.apk`。

如果没自动触发，点 **Run workflow**（右上角）。

### 4. 装到手机

把 `app-debug.apk` 传到手机（微信文件传输助手、数据线、或电脑上
`python -m http.server` 后手机浏览器下载），点开安装，允许一次「未知来源」。

打开 App：应直接显示总览页。连不上就**长按顶部细条**改成 Pi 的实际地址。

## 二、本机编译（有 JDK 17 + Android SDK 时）

```bash
gradle assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

或用 Android Studio 打开本目录 → `Build → Build APK(s)`。

## 三、改默认地址

`app/build.gradle.kts`：

```kotlin
buildConfigField("String", "DEFAULT_URL", "\"http://10.42.0.1:8080\"")
```

改完重新构建。页面内容仍来自树莓派，改网页不需要重新打包。

## 注意

- 用的是 debug 签名，可以直接安装；上架应用商店要自建 keystore；
- 比赛前确认 Pi 的 `config.json` 里 `demo_mode` 是 `false`，否则这个 App 等于把管理员入口带在身上。
