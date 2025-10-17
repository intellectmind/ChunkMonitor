# ChunkMonitor - 区块性能监控插件

**其他语言版本: [English](README.md)，[中文](README_zh.md)。**

----------------------------------------------------------------------------------------------------------

一个 Minecraft 服务器区块MSPT占用、掉落物、实体监控插件，超过设定的值时，会向玩家、控制台发送通知。

## 功能特性

### 核心监控功能

- **MSPT 监控** - 实时监控单区块加载时间（毫秒每刻）
- **实体数量监控** - 统计单区块内实体总数，防止卡顿
- **掉落物监控** - 统计单区块内掉落物数量，及时发现物品堆积问题

### 通知系统

- **双向通知** - 支持公屏通知和控制台通知独立控制
- **冷却机制** - 同一区块 5 分钟内只通知一次，防止刷屏
- **精确定位** - 显示世界名称、区块坐标和具体方块坐标范围
- **多语言支持** - 内置中英文，可自定义消息模板

## 配置文件


```yaml
# 区块监控插件配置文件
# Chunk Monitor Plugin Configuration File
# 所有时间间隔单位为 Ticks (1秒 = 20 Ticks)
# All time intervals are in Ticks (1 second = 20 Ticks)

# 语言设置 / Language Setting
# 支持: zh_CN (简体中文), en_US (English)
# Supported: zh_CN (Simplified Chinese), en_US (English)
language: "zh_CN"

# MSPT (毫秒每刻) 监控配置 / MSPT Monitoring Configuration
mspt:
  enabled: true
  interval: 100  # 检测间隔，单位 Ticks / Detection interval in Ticks
  notification-limit: 50.0  # 通知上限，单位毫秒 / Notification limit in milliseconds

# 实体数量监控配置 / Entity Count Monitoring Configuration
entity:
  enabled: true
  interval: 80  # 检测间隔，单位 Ticks / Detection interval in Ticks
  notification-limit: 200  # 区块内实体数量上限 / Entity count limit per chunk

# 掉落物数量监控配置 / Item Drop Monitoring Configuration
item:
  enabled: true
  interval: 60  # 检测间隔，单位 Ticks / Detection interval in Ticks
  notification-limit: 100  # 区块内掉落物数量上限 / Item drop count limit per chunk

# 冷却配置 / Cooldown Configuration
cooldown:
  duration: 5  # 通知冷却时间，单位分钟 / Notification cooldown in minutes

# 通知配置 / Notification Configuration
notification:
  broadcast: true  # 是否向全服通知 / Send notification to all players
  console: true  # 是否向控制台通知 / Send notification to console
  show-coordinates: true  # 是否显示坐标范围 / Show coordinate range

# 消息配置 / Message Configuration
messages:
  zh_CN:
    # MSPT 通知消息
    mspt_alert: "§c[区块监控] §e世界: %world% §r| §e区块: [%chunk_x%, %chunk_z%] §r| §e坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cMSPT: %value% ms §r(上限: %limit% ms)"

    # 实体数量通知消息
    entity_alert: "§c[区块监控] §e世界: %world% §r| §e区块: [%chunk_x%, %chunk_z%] §r| §e坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §c实体数量: %value% §r(上限: %limit%)"

    # 掉落物通知消息
    item_alert: "§c[区块监控] §e世界: %world% §r| §e区块: [%chunk_x%, %chunk_z%] §r| §e坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §c掉落物: %value% §r(上限: %limit%)"

    # 启用消息
    enabled_message: "§a[区块监控] 插件已加载"
    server_type_message: "§6服务器类型: %type%"
    disabled_message: "§c[区块监控] 插件已卸载"

  en_US:
    # MSPT Alert Message
    mspt_alert: "§c[ChunkMonitor] §eWorld: %world% §r| §eChunk: [%chunk_x%, %chunk_z%] §r| §eCoordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cMSPT: %value% ms §r(Limit: %limit% ms)"

    # Entity Count Alert Message
    entity_alert: "§c[ChunkMonitor] §eWorld: %world% §r| §eChunk: [%chunk_x%, %chunk_z%] §r| §eCoordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cEntity Count: %value% §r(Limit: %limit%)"

    # Item Drop Alert Message
    item_alert: "§c[ChunkMonitor] §eWorld: %world% §r| §eChunk: [%chunk_x%, %chunk_z%] §r| §eCoordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cItem Drops: %value% §r(Limit: %limit%)"

    # Enable Message
    enabled_message: "§a[ChunkMonitor] Plugin enabled"
    server_type_message: "§6Server Type: %type%"
    disabled_message: "§c[ChunkMonitor] Plugin disabled"
```
