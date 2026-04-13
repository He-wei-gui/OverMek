# 更新日志

## 2026-04-12

### 包名改为Java规范（全小写）
- **变更前**: `com.HeWeigui.overmek`
- **变更后**: `com.hewiegui.overmek`

### 具体改动
1. 重命名包目录 `com/HeWeigui` → `com/hewiegui`
2. 更新两个 Java 文件的 package 声明
3. 更新 `gradle.properties` 中的 `mod_group_id`
4. 删除旧目录 `src/main/java/com/HeWeigui/`

### 受影响文件
- `src/main/java/com/hewiegui/overmek/ExampleMod.java` (更新 package)
- `src/main/java/com/hewiegui/overmek/Config.java` (更新 package)
- `gradle.properties` (修改: mod_group_id)
- `src/main/java/com/HeWeigui/` (删除)

---

### 2026-04-12

#### 包名重构
- **变更前**: `com.example.examplemod`
- **变更后**: `com.hewiegui.overmek`

#### 具体改动
1. 创建新包 `src/main/java/com/hewiegui/overmek/`
2. 移动并更新 `ExampleMod.java` - package 声明
3. 移动并更新 `Config.java` - package 声明
4. 更新 `gradle.properties` 中的 `mod_group_id`
5. 删除旧包目录 `src/main/java/com/example/`

#### 受影响文件
- `src/main/java/com/hewiegui/overmek/ExampleMod.java` (新建)
- `src/main/java/com/hewiegui/overmek/Config.java` (新建)
- `gradle.properties` (修改: mod_group_id)
- `src/main/java/com/example/examplemod/` (删除)

---

*每次更新请在此文件顶部添加新的条目*