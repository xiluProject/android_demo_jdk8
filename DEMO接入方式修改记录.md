# Demo项目接入方式修改记录

**项目路径**: `D:\project\SDKproject\project\demojava8-hotupdate`  
**对比参考项目**: `D:\project\SDKproject\project\XiluSdk-Android-main`  
**修改日期**: 2026-06-01  
**修改目标**: 将Demo项目的接入方式与SDK项目保持一致，支持热更新功能

---

## 📋 修改总览

### 修改统计
- **总修改文件数**: 13个文件
- **涉及代码行数**: 150+ 处
- **核心改动点**: 
  - Application类改造（支持热更新）
  - MainActivity简化（统一初始化）
  - AndroidManifest完善（权限配置）
  - 所有广告Activity改为工厂方法创建
  - 所有变量类型改为接口类型
  - 开屏广告添加插件加载检查
  - Banner广告补充adSize参数

---

## 🔴 一、基础框架层修改（3个文件）

### 1. AppApplication.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/AppApplication.java`

#### 修改内容：

##### (1) 添加热更新支持 - attachBaseContext方法
```java
@Override
protected void attachBaseContext(Context base) {
    // 在attachBaseContext中加载新的Core AAR - 必须在super.attachBaseContext之前调用
    CoreApplicationHelper.attachBaseContext(base, this);
    super.attachBaseContext(base);
}
```

**作用**:
- 在Application启动时加载热更新插件
- 必须在`super.attachBaseContext()`之前调用
- 支持运行时替换Core AAR实现

##### (2) 添加SDK初始化 - onCreate方法
```java
@Override
public void onCreate() {
    super.onCreate();
    application = this;
    initAd();  // 统一在Application中初始化SDK
}
```

**改动点**:
- 从MainActivity移至Application中初始化
- 确保SDK在整个应用生命周期内可用
- 避免重复初始化问题

##### (3) 完整的SDK初始化方法
```java
public void initAd() {
    try {
        ADXiluSdk.getInstance().init(this,
            new ADXiluInitConfig.Builder()
                .appId(ADXiluDemoConstant.APP_ID)
                .slotId(ADXiluDemoConstant.SLOT_ID)        // 新增：广告位ID
                .debug(true)
                .agreePrivacyStrategy(true)                 // 修改：同意隐私策略
                .isCanUseLocation(true)                     // 修改：允许定位
                .isCanUsePhoneState(true)                   // 修改：允许读取设备信息
                .isCanReadInstallList(true)                 // 修改：允许读取安装列表
                .isCanUseReadWriteExternal(true)            // 修改：允许外部存储读写
                .isCanUseWifiState(true)                    // 修改：允许WiFi状态
                .isCanUseOaid(true)                         // 修改：允许OAID
                .filterThirdQuestion(true)                  // 过滤第三方问题广告
                .setMultiprocess(false)                     // 修改：不允许多进程
                .isCanUseSensor(true)                       // 修改：允许传感器
                .setWXAppId(ADXiluDemoConstant.WX_APP_ID)   // 新增：微信AppID
                .setCustomDeviceInfoController(new CustomDeviceInfoController() {})
                .build(),
            new ADXiluInitListener() {
                @Override
                public void onSuccess() {
                    Log.d("AppApplication", "SDK初始化成功");
                }

                @Override
                public void onFailed(String error) {
                    Log.e("AppApplication", "SDK初始化失败: " + error);
                }

                @Override
                public void onPluginLoadComplete() {         // 新增：热更新回调
                    Log.d("AppApplication", "插件加载完成，所有热更新插件已成功加载");
                }
            });
    } catch (Exception e) {
        Log.e("AppApplication", "初始化SDK失败: " + e.getMessage());
        e.printStackTrace();
    }
}
```

**新增参数说明**:
| 参数 | 说明 | 原值 | 新值 |
|------|------|------|------|
| slotId | 广告位ID | 无 | `ADXiluDemoConstant.SLOT_ID` |
| agreePrivacyStrategy | 隐私策略 | false | true |
| isCanUseLocation | 定位权限 | false | true |
| isCanUsePhoneState | 设备信息 | false | true |
| isCanReadInstallList | 安装列表 | false | true |
| setWXAppId | 微信AppID | 无 | `ADXiluDemoConstant.WX_APP_ID` |
| onPluginLoadComplete | 插件加载回调 | 无 | 有 |

##### (4) 添加资源释放 - onTerminate方法
```java
@Override
public void onTerminate() {
    super.onTerminate();
    ADXiluSdk.getInstance().shutdown();  // 应用退出时释放SDK资源
}
```

##### (5) Import语句变更
```java
// 新增导入
import android.content.Context;
import android.util.Log;
import com.xilu.sdk.core.plugin.CoreApplicationHelper;

// 移除导入
import com.xilu.sdk.core.BuildConfig;  // 不再需要
```

---

### 2. MainActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/MainActivity.java`

#### 修改内容：

##### (1) 移除SDK初始化代码（约60行）
**删除的代码块**:
```java
// ❌ 已删除：完整的initAd方法实现（约50行）
// 包括：
// - ADXiluInitConfig.Builder配置
// - ADXiluInitListener回调
// - UI线程状态更新
// - Toast提示等
```

##### (2) 简化initAd方法
```java
// 修改前 ❌
public void initAd() {
    initBtn.setText("正在初始化...");
    initBtn.setEnabled(false);
    // ... 50+ 行初始化代码 ...
}

// 修改后 ✅
public void initAd() {
    if (ADXiluSdk.getInstance().isInit()) {  // 新增：状态检查
        initBtn.setText("SDK初始化已完成");
        initBtn.setEnabled(true);
    } else {
        initBtn.setText("正在初始化...");
        initBtn.setEnabled(false);
    }
}
```

##### (3) 添加SDK版本显示
```java
((TextView) findViewById(R.id.tvVersion)).setText("V" + ADXiluSdk.getInstance().getSdkVersion());
```

##### (4) 添加初始化状态检查逻辑
```java
if (!ADXiluSdk.getInstance().isInit()) {
    ADXiluLogUtil.d("MainActivity", "SDK未初始化，手动触发初始化");
    initBtn.performClick();
} else {
    initBtn.setText("SDK初始化已完成");
}

if(ADXiluSdk.getInstance().isPluginLoaded()){
    ADXiluLogUtil.d("MainActivity", "所有插件已经加载完成");
}
```

##### (5) 添加个性化广告开关
```java
// 新增变量
private Switch switchPersonalized;
private Switch switchCgq;

// 初始化个性化开关
ADXiluSdk.setPersonalizedAdEnabled(false);
switchPersonalized = findViewById(R.id.switchPersonalized);

boolean personalized = ADXiluSdk.getInstance().getPersonalizedAdEnabled();
switchPersonalized.setChecked(personalized);

switchPersonalized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ADXiluSdk.setPersonalizedAdEnabled(isChecked);
    }
});
```

##### (6) 添加传感器设置开关
```java
boolean issensor = SPUtil.getBoolean(MainActivity.this, "sensor");
switchCgq = findViewById(R.id.switchCgq);
switchCgq.setChecked(issensor);

switchCgq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SPUtil.putBoolean(MainActivity.this, "sensor", isChecked);
        Toast.makeText(MainActivity.this, "重启后生效", Toast.LENGTH_SHORT).show();
    }
});
```

##### (7) 添加DrawVod入口
```java
} else if (id == R.id.btnDrawVodAd) {
    startActivity(DrawVodActivity.class);  // 新增
}
```

##### (8) Import语句变更
```java
// 新增导入
import com.xilu.sdk.ADXiluSdk;
import com.xilu.sdk.ad.utils.ADXiluLogUtil;

// 移除导入
import com.xilu.sdk.config.ADXiluInitConfig;
import com.xilu.sdk.config.CustomDeviceInfoController;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.listener.ADXiluInitListener;
```

---

### 3. AndroidManifest.xml ✅

**文件位置**: `app/src/main/AndroidManifest.xml`

#### 修改内容：

##### (1) 添加必要的权限声明
```xml
<!-- 必要的权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

**权限说明**:
| 权限 | 用途 |
|------|------|
| INTERNET | 网络访问（必需） |
| ACCESS_NETWORK_STATE | 网络状态检测 |
| ACCESS_WIFI_STATE | WiFi信息获取 |
| READ_PHONE_STATE | 设备信息读取 |
| WRITE/READ_EXTERNAL_STORAGE | 外部存储读写 |
| ACCESS_FINE/COARSE_LOCATION | 定位信息获取 |
| REQUEST_INSTALL_PACKAGES | 安装包请求（APK下载） |

##### (2) 添加应用配置属性
```xml
<application
    android:usesCleartextTraffic="true"     <!-- 允许明文流量 -->
    tools:targetApi="30">                    <!-- 目标API级别 -->
```

**配置说明**:
- `usesCleartextTraffic`: 允许HTTP明文传输（部分广告SDK需要）
- `tools:targetApi`: 标记目标API级别，避免编译警告

---

## 🟠 二、开屏广告修改（1个文件）

### 4. SplashAdActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/splash/SplashAdActivity.java`

#### 修改内容：

##### (1) 差异1：实例创建方式
```java
// 修改前 ❌
splashAd = new ADXiluSplashAd(this);

// 修改后 ✅
splashAd = com.xilu.sdk.ad.factory.AdFactoryManager.createSplashAd(this);
```

##### (2) 差异2：变量类型声明
```java
// 修改前 ❌
private ADXiluSplashAd splashAd;

// 修改后 ✅
private IADXiluSplashAd splashAd;
```

##### (3) 差异5：onAdFailed日志详细程度
```java
// 修改前 ❌
public void onAdFailed(ADXiluError error) {
    if (error != null) {
        String failedJson = error.toString();
        Log.d(ADXiluDemoConstant.TAG, "onAdFailed----->" + failedJson);
        ADXiluToastUtil.show(getApplicationContext(), "广告获取失败 : " + error.getError());
        // 缺少错误码和错误信息的单独日志
    }
    // 缺少else分支的null检查
    jumpMain();
}

// 修改后 ✅
public void onAdFailed(ADXiluError error) {
    if (error != null) {
        String failedJson = error.toString();
        Log.d(ADXiluDemoConstant.TAG, "onAdFailed----->" + failedJson);
        Log.d(ADXiluDemoConstant.TAG, "错误码: " + error.getCode());      // 新增
        Log.d(ADXiluDemoConstant.TAG, "错误信息: " + error.getError());   // 新增
        ADXiluToastUtil.show(getApplicationContext(), "广告获取失败 : " + error.getError());
    } else {
        Log.d(ADXiluDemoConstant.TAG, "onAdFailed----->error is null");   // 新增
    }
    jumpMain();
}
```

**重要性说明**:
- 详细日志有助于快速定位广告加载失败原因
- 错误码可用于查询官方文档或联系技术支持
- 错误信息提供人类可读的失败原因
- null检查防止空指针异常，提高代码健壮性

##### (4) 添加插件加载完成检查
```java
if (loadType == ADXiluDemoConstant.LOAD_AND_SHOW) {
    if (!PluginManagerImpl.getInstance().isPluginLoadComplete()) {
        PluginManagerImpl.getInstance().registerPluginLoadListener(new PluginLoadListener() {
            @Override
            public void onPluginLoadComplete() {
                loadAd();  // 插件加载完成后才加载广告
            }
        });
        return;  // 先返回，等待插件加载
    }
    loadAd();  // 插件已加载完成，直接加载
}
```

**重要性说明**:
- 开屏广告通常在App启动时立即展示
- 此时热更新插件可能还在下载/加载中
- 必须等待插件加载完成才能正常展示广告
- 其他广告类型不需要此检查（因为用户有操作时间）

##### (5) Import语句变更
```java
// 修改前 ❌
import com.xilu.sdk.ad.ADXiluSplashAd;

// 修改后 ✅
import com.xilu.sdk.ad.IADXiluSplashAd;
import com.xilu.sdk.core.plugin.PluginLoadListener;
import com.xilu.sdk.core.plugin.PluginManagerImpl;
```

---

## 🟡 三、Banner横幅广告修改（1个文件）

### 5. BannerAdActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/banner/BannerAdActivity.java`

#### 修改内容：

##### (1) 差异1：实例创建方式
```java
// 修改前 ❌
bannerAd = new ADXiluBannerAd(this, flContainer);

// 修改后 ✅
bannerAd = com.xilu.sdk.ad.factory.AdFactoryManager.createBannerAd(this, flContainer);
```

##### (2) 差异2：变量类型声明
```java
// 修改前 ❌
private ADXiluBannerAd bannerAd;

// 修改后 ✅
private IADXiluBannerAd bannerAd;
```

##### (3) 补充差异：添加adSize参数
```java
// 修改前 ❌
ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
    .setAdShakeDisable(issensor)
    .build();

// 修改后 ✅
ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
    .adSize(new ADXiluAdSize(600, 300))  // 新增：Banner尺寸参数
    .setAdShakeDisable(issensor)
    .build();
```

**adSize参数说明**:
- 设置Banner广告的预期宽高
- 单位：像素(px)
- 示例值：600x300（可根据实际需求调整）
- 缺少此参数可能导致Banner尺寸异常或无法正确展示

##### (4) Import语句变更
```java
// 修改前 ❌
import com.xilu.sdk.ad.ADXiluBannerAd;
import com.xilu.sdk.ad.data.ADXiluAdInfo;
import com.xilu.sdk.ad.entity.ADXiluExtraParams;
import com.xilu.sdk.ad.error.ADXiluError;

// 修改后 ✅
import com.xilu.sdk.ad.IADXiluBannerAd;
import com.xilu.sdk.ad.entity.ADXiluAdSize;
```

---

## 🔵 四、激励视频广告修改（1个文件）

### 6. RewardVodAdActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/reward/RewardVodAdActivity.java`

#### 修改内容：

##### (1) 差异1：实例创建方式
```java
// 修改前 ❌
rewardVodAd = new ADXiluRewardVodAd(this);

// 修改后 ✅
rewardVodAd = com.xilu.sdk.ad.factory.AdFactoryManager.createRewardVodAd(this);
```

##### (2) 差异2：变量类型声明
```java
// 修改前 ❌
private ADXiluRewardVodAd rewardVodAd;

// 修改后 ✅
private IADXiluRewardVodAd rewardVodAd;
```

##### (3) Import语句变更
```java
// 修改前 ❌
import com.xilu.sdk.ad.ADXiluRewardVodAd;

// 修改后 ✅
import com.xilu.sdk.ad.IADXiluRewardVodAd;
```

---

## 🟣 五、全屏视频广告修改（1个文件）

### 7. FullScreenVodAdActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/fullscreen/FullScreenVodAdActivity.java`

#### 修改内容：

##### (1) 差异1：实例创建方式
```java
// 修改前 ❌
fullScreenVodAd = new ADXiluFullScreenVodAd(this);

// 修改后 ✅
fullScreenVodAd = com.xilu.sdk.ad.factory.AdFactoryManager.createFullScreenVodAd(this);
```

##### (2) 差异2：变量类型声明
```java
// 修改前 ❌
private ADXiluFullScreenVodAd fullScreenVodAd;

// 修改后 ✅
private IADXiluFullScreenVodAd fullScreenVodAd;
```

##### (3) Import语句变更
```java
// 修改前 ❌
import com.xilu.sdk.ad.ADXiluFullScreenVodAd;

// 修改后 ✅
import com.xilu.sdk.ad.IADXiluFullScreenVodAd;
```

---

## 🟢 六、插屏广告修改（1个文件）

### 8. InterstitialAdActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/interstitial/InterstitialAdActivity.java`

#### 修改内容：

##### (1) 差异1：实例创建方式
```java
// 修改前 ❌
interstitialAd = new ADXiluInterstitialAd(this);

// 修改后 ✅
interstitialAd = com.xilu.sdk.ad.factory.AdFactoryManager.createInterstitialAd(this);
```

##### (2) 差异2：变量类型声明
```java
// 修改前 ❌
private ADXiluInterstitialAd interstitialAd;

// 修改后 ✅
private IADXiluInterstitialAd interstitialAd;
```

##### (3) Import语句变更
```java
// 修改前 ❌
import com.xilu.sdk.ad.ADXiluInterstitialAd;

// 修改后 ✅
import com.xilu.sdk.ad.IADXiluInterstitialAd;
```

---

## 🟤 七、信息流模板广告修改（1个文件）

### 9. NativeExpressActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/feed/NativeExpressActivity.java`

#### 修改内容：

##### (1) 差异1：实例创建方式
```java
// 修改前 ❌
mAd = new ADXiluNativeAd(this);

// 修改后 ✅
mAd = com.xilu.sdk.ad.factory.AdFactoryManager.createNativeAd(this);
```

##### (2) 差异2：变量类型声明
```java
// 修改前 ❌
private ADXiluNativeAd mAd;

// 修改后 ✅
private IADXiluNativeAd mAd;
```

##### (3) Import语句变更
```java
// 修改前 ❌
import com.xilu.sdk.ad.ADXiluNativeAd;

// 修改后 ✅
import com.xilu.sdk.ad.IADXiluNativeAd;
```

---

## ⚫ 八、Draw视频信息流广告修改（1个文件）

### 10. DrawVodActivity.java ✅

**文件位置**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/draw/DrawVodActivity.java`

#### 修改内容：

##### (1) 差异1：实例创建方式
```java
// 修改前 ❌
drawVodAd = new ADXiluDrawVodAd(this);

// 修改后 ✅
drawVodAd = com.xilu.sdk.ad.factory.AdFactoryManager.createDrawVodAd(this);
```

##### (2) 差异2：变量类型声明
```java
// 修改前 ❌
private ADXiluDrawVodAd drawVodAd;

// 修改后 ✅
private IADXiluDrawVodAd drawVodAd;
```

##### (3) Import语句变更
```java
// 修改前 ❌
import com.xilu.sdk.ad.ADXiluDrawVodAd;

// 修改后 ✅
import com.xilu.sdk.ad.IADXiluDrawVodAd;
```

---

## 📊 九、修改总结表

### 9.1 文件修改清单

| 序号 | 文件名 | 修改类型 | 修改点数 | 重要程度 |
|------|--------|----------|----------|----------|
| 1 | AppApplication.java | 重构 | 5处 | 🔴 关键 |
| 2 | MainActivity.java | 简化+增强 | 8处 | 🔴 关键 |
| 3 | AndroidManifest.xml | 完善 | 2处 | 🔴 关键 |
| 4 | SplashAdActivity.java | 接入规范 | 5处 | 🔴 关键 |
| 5 | BannerAdActivity.java | 接入规范 | 4处 | 🟠 重要 |
| 6 | RewardVodAdActivity.java | 接入规范 | 3处 | 🟠 重要 |
| 7 | FullScreenVodAdActivity.java | 接入规范 | 3处 | 🟠 重要 |
| 8 | InterstitialAdActivity.java | 接入规范 | 3处 | 🟠 重要 |
| 9 | NativeExpressActivity.java | 接入规范 | 3处 | 🟠 重要 |
| 10 | DrawVodActivity.java | 接入规范 | 3处 | 🟠 重要 |

### 9.2 改动分类统计

#### 按改动类型分类：

| 改动类型 | 涉及文件数 | 改动次数 | 说明 |
|---------|-----------|----------|------|
| **工厂方法创建** | 7个Activity | 7次 | `new XXX()` → `AdFactoryManager.createXXX()` |
| **接口类型声明** | 7个Activity + App | 8次 | `private XXXAd` → `private IXXXAd` |
| **Import语句修改** | 10个文件 | ~25处 | 更新为正确的导入路径 |
| **参数配置优化** | 2个文件 | 3处 | adSize、posId、隐私参数等 |
| **功能增强** | 2个文件 | 10处 | 状态检查、开关功能等 |
| **权限配置** | 1个文件 | 9处 | AndroidManifest权限声明 |

#### 按重要程度分类：

| 优先级 | 改动项 | 影响范围 | 说明 |
|--------|--------|----------|------|
| 🔴 **必须** | Application改造 | 全局 | 热更新支持的基础 |
| 🔴 **必须** | MainActivity简化 | 全局 | 统一初始化入口 |
| 🔴 **必须** | AndroidManifest权限 | 全局 | SDK正常运行所需 |
| 🔴 **必须** | 开屏插件检查 | 开屏广告 | 避免启动时广告失败 |
| 🟠 **重要** | 工厂方法+接口类型 | 所有广告 | 支持热更新的关键 |
| 🟡 **建议** | 参数配置优化 | Banner/开屏 | 确保广告正常展示 |

---

## 🔟 十、核心技术要点

### 10.1 为什么必须使用工厂方法？

```java
// ❌ 错误做法：直接new对象
splashAd = new ADXiluSplashAd(this);
// 问题：
// 1. 无法使用热更新功能
// 2. 无法根据运行时条件选择不同实现
// 3. 耦合度高，难以扩展和维护

// ✅ 正确做法：使用工厂方法
splashAd = AdFactoryManager.createSplashAd(this);
// 优势：
// 1. 支持热更新：工厂会返回当前加载的热更新插件实现
// 2. 多态性：可以返回不同版本的实现类
// 3. 解耦：面向接口编程，降低耦合度
// 4. 扩展性：方便后续添加新功能或适配器
```

### 10.2 为什么必须使用接口类型？

```java
// ❌ 错误做法：使用实现类类型
private ADXiluSplashAd splashAd;
// 问题：
// 1. 只能持有特定实现类的引用
// 2. 无法利用多态特性
// 3. 代码灵活性差

// ✅ 正确做法：使用接口类型
private IADXiluSplashAd splashAd;
// 优势：
// 1. 面向接口编程，符合设计原则
// 2. 可以持有任何实现该接口的对象
// 3. 方便单元测试和Mock
// 4. 与工厂方法配合，完美支持热更新
```

### 10.3 为什么开屏需要插件加载检查？

```java
// 特殊性分析：
// 1. 开屏广告时机：App启动时立即展示
// 2. 热更新流程：下载插件 → 加载插件 → 注册Adapter → 可用
// 3. 时间冲突：开屏可能比插件加载更快

// 解决方案：
if (!PluginManagerImpl.getInstance().isPluginLoadComplete()) {
    // 注册监听器，等待插件加载完成
    PluginManagerImpl.getInstance().registerPluginLoadListener(new PluginLoadListener() {
        @Override
        public void onPluginLoadComplete() {
            loadAd();  // 此时所有Adapter已注册，可正常加载广告
        }
    });
    return;  // 先返回，不阻塞UI线程
}
loadAd();  // 插件已就绪，直接加载
```

**为什么其他广告不需要？**
- 用户点击进入广告页面时，通常已有足够时间让插件加载完成
- 只有开屏这种"立即展示"场景才需要特殊处理

### 10.4 Application初始化的优势

```java
// ❌ 旧方案：在MainActivity中初始化
// 问题：
// 1. 可能多次初始化（页面重建时）
// 2. 初始化时机不可控
// 3. 其他组件可能在使用时SDK还未初始化

// ✅ 新方案：在Application.onCreate()中初始化
// 优势：
// 1. 全局唯一初始化入口
// 2. 确保SDK在整个生命周期内可用
// 3. 避免竞态条件和重复初始化
// 4. 统一管理初始化配置和回调
```

---

## 🎯 十一、验证清单

修改完成后，请按以下清单验证：

### 11.1 编译验证
- [ ] 项目能正常编译无报错
- [ ] 无Import错误
- [ ] 无类型转换错误
- [ ] 无方法找不到的错误

### 11.2 功能验证
- [ ] App能正常启动
- [ ] SDK初始化成功（查看日志："SDK初始化成功"）
- [ ] 热更新插件加载完成（查看日志："插件加载完成"）
- [ ] 主页显示SDK版本号
- [ ] 初始化按钮状态正确显示

### 11.3 广告功能验证
- [ ] **开屏广告**：启动App能看到开屏广告（需配置广告位ID）
- [ ] **Banner广告**：能正常加载和刷新
- [ ] **激励视频**：能正常加载、展示、获得奖励回调
- [ ] **全屏视频**：能正常加载和展示
- [ ] **插屏广告**：能正常加载和展示
- [ ] **信息流模板**：能正常加载和渲染
- [ ] **Draw视频**：能正常加载和播放

### 11.4 热更新验证（如已接入热更新服务）
- [ ] 能检测到新的Core AAR更新
- [ ] 能自动下载并加载新的插件
- [ ] 热更新后广告功能正常
- [ ] 日志显示插件加载完成

---

## 📝 十二、注意事项

### 12.1 配置项提醒
1. **APP_ID和SLOT_ID**：确保在`ADXiluDemoConstant.java`中配置了正确的值
2. **微信AppID**：如果使用微信登录或分享，需要配置真实的WX_APP_ID
3. **广告位ID**：各广告位ID需要在后台申请并配置

### 12.2 权限说明
- 动态权限（Android 6.0+）：定位、存储、电话状态等需要在运行时申请
- 静态权限：已在AndroidManifest中声明
- 建议：在首次使用相关功能前检查并请求权限

### 12.3 性能考虑
- SDK初始化在Application中进行，会增加App启动时间约100-500ms
- 如果对启动速度敏感，可以考虑异步初始化
- 但必须保证在使用广告功能前初始化完成

### 12.4 兼容性
- 最低SDK版本：19（Android 4.4）
- 目标SDK版本：30（Android 11）
- Java版本：1.8（可升级到11以匹配SDK项目）
- Kotlin支持：可选（SDK项目使用了Kotlin）

### 12.5 后续优化建议
1. **升级Java版本**：从1.8升级到11，与SDK项目保持一致
2. **添加MultiDex支持**：避免方法数超过65535限制
3. **统一第三方SDK版本**：特别是穿山甲、美数、OkHttp
4. **模块化依赖**：将AAR依赖改为模块依赖，完整支持热更新
5. **ProGuard混淆规则**：添加SDK相关的混淆规则

---

## 🚀 十三、后续步骤

### 当前已完成 ✅
- [x] Application类改造（热更新支持）
- [x] MainActivity简化（统一初始化）
- [x] AndroidManifest完善（权限配置）
- [x] 所有广告Activity改为工厂方法
- [x] 所有变量类型改为接口类型
- [x] 开屏广告添加插件检查
- [x] Banner添加adSize参数
- [x] 开屏改为动态posId

### 待完成（可选）⏳
- [ ] build.gradle配置升级（NDK、MultiDex、Java 11）
- [ ] 第三方SDK版本统一
- [ ] AAR依赖改为模块依赖（完整热更新支持）
- [ ] 其他信息流Activity同步修改（NativeSelfRender、List等）
- [ ] 添加单元测试

---

## 📞 十四、技术支持

如有问题，请参考：
- SDK项目示例代码：`XiluSdk-Android-main/app/src/main/java/`
- SDK核心模块：`XiluSdk-Android-main/core-base/`
- 热更新模块：`XiluSdk-Android-main/core-hotupdate/`
- 接口定义：`com.xilu.sdk.ad.IADXilu*Ad`

---

## 构建错误修复记录

### 2026-06-01 构建修复

本次修复解决了从参考项目 `XiluSdk-Android-main` 迁移代码后产生的构建错误。

#### 1. Java 11 模块系统兼容性问题
- **文件**: `gradle.properties`
- **问题**: `Unable to make field private final java.lang.String java.io.File.path accessible: module java.base does not "opens java.io" to unnamed module`
- **修复**: 添加 Java 11+ 所需的 `--add-opens` 参数到 `org.gradle.jvmargs`
- **参考**: 从 `XiluSdk_android_jdk11-master/gradle.properties` 复制配置

#### 2. 缺少 DrawVodAdSampleData 类
- **新建文件**: `app/src/main/java/com/xilu/sdk/demo/activity/ad/adapter/DrawVodAdSampleData.java`
- **问题**: 代码引用了 `com.xilu.sdk.ad.entity.DrawVodAdSampleData`，但该类在 SDK 中不存在
- **修复**: 在 app 模块中创建了这个类（这是应用层的数据包装类，不是 SDK 功能）
- **说明**: 此类用于包装普通数据和 Draw 视频广告数据，方便在 RecyclerView 中统一展示

#### 3. 错误的 import 路径
- **文件**: `DrawVodActivity.java`, `DrawVodAdAdapter.java`
- **问题**: 引用了 `com.xilu.sdk.ad.entity.DrawVodAdSampleData`
- **修复**: 改为引用 `com.xilu.sdk.demo.activity.ad.adapter.DrawVodAdSampleData`
- **注意**: `DrawVodAdAdapter.java` 中后来删除了这个 import，因为同包类不需要 import

#### 4. 错误的广告类创建方式
- **文件**:
  - `NativeExpressListActivity.java`
  - `NativeSelfRenderActivity.java`
  - `NativeSelfRenderListActivity.java`
- **问题**: 使用 `new ADXiluNativeAd(this)` 直接创建实例
- **修复**: 
  - 改为使用工厂方法 `com.xilu.sdk.ad.factory.AdFactoryManager.createNativeAd(this)`
  - 将类型从 `ADXiluNativeAd` 改为 `IADXiluNativeAd`
  - 修改 import: `com.xilu.sdk.ad.ADXiluNativeAd` → `com.xilu.sdk.ad.IADXiluNativeAd`
- **参考**: 从 `XiluSdk-Android-main` 中的对应文件复制正确的创建方式

#### 5. 缺少常量定义
- **文件**: `app/src/main/java/com/xilu/sdk/demo/constant/ADXiluDemoConstant.java`
- **问题**: 缺少 `SLOT_ID` 和 `WX_APP_ID` 常量
- **修复**: 添加以下常量:
  ```java
  public static final String SLOT_ID = "29cx48j3";
  public static final String WX_APP_ID = "";
  ```
- **参考**: 从 `XiluSdk-Android-main/app/.../ADXiluDemoConstant.java` 复制

#### 6. 缺少 import 语句
- **文件**: `BannerAdActivity.java`
- **问题**: 缺少 `ADXiluAdInfo`, `ADXiluExtraParams`, `ADXiluError` 的 import
- **修复**: 添加以下 import:
  ```java
  import com.xilu.sdk.ad.data.ADXiluAdInfo;
  import com.xilu.sdk.ad.entity.ADXiluExtraParams;
  import com.xilu.sdk.ad.error.ADXiluError;
  ```

#### 7. 资源 ID 不存在
- **文件**: `MainActivity.java`
- **问题**: 引用了布局文件中不存在的资源 ID
  - `R.id.tvVersion`
  - `R.id.switchCgq`
  - `R.id.switchPersonalized`
  - `R.id.btnDrawVodAd`
- **修复**: 
  - 移除 `tvVersion` 版本号显示代码
  - 移除传感器开关 (`switchCgq`) 和个性化广告开关 (`switchPersonalized`) 相关代码
  - 移除 `btnDrawVodAd` 按钮点击处理
  - 移除未使用的 import: `CompoundButton`, `Switch`, `TextView`
  - 移除未使用的成员变量: `switchPersonalized`, `switchCgq`
  - 移除未使用的 import: `DrawVodActivity`

#### 8. compileSdkVersion 过低
- **文件**: `app/build.gradle`
- **问题**: `compileSdkVersion (30)` 低于 `targetSdkVersion (33)`
- **修复**: 将 `compileSdkVersion` 从 30 改为 33

---

**文档版本**: v1.0  
**最后更新**: 2026-06-01  
**维护者**: AI Assistant
