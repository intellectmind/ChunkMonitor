# ChunkMonitor - Block Performance Monitoring Plugin

**Read this in other languages: [English](README.md)，[中文](README_zh.md)。**

----------------------------------------------------------------------------------------------------------

A Minecraft server block MSPT usage, dropped items, and entity monitoring plugin. When values exceed the configured threshold, notifications are sent to players and console.

## Feature Highlights

### Core Monitoring Functions

- **MSPT Monitoring** - Real-time monitoring of individual chunk loading time (milliseconds per tick)
- **Entity Count Monitoring** - Statistics for total entities within a chunk, preventing lag
- **Dropped Items Monitoring** - Statistics for dropped items within a chunk, promptly detecting item accumulation issues

### Notification System

- **Dual-Channel Notifications** - Support for independent control of in-game chat notifications and console notifications
- **Cooldown Mechanism** - Only notify once per chunk within 5 minutes, preventing spam
- **Precise Localization** - Displays world name, chunk coordinates, and specific block coordinate ranges
- **Multi-Language Support** - Built-in Chinese and English, customizable message templates

## Configuration File

```yaml
# Chunk Monitor Plugin Configuration File
# All time intervals are in Ticks (1 second = 20 Ticks)

# Language Setting
# Supported: zh_CN (Simplified Chinese), en_US (English)
language: "zh_CN"

# MSPT Monitoring Configuration
mspt:
  enabled: true
  interval: 100  # Detection interval in Ticks
  notification-limit: 50.0  # Notification limit in milliseconds

# Entity Count Monitoring Configuration
entity:
  enabled: true
  interval: 80  # Detection interval in Ticks
  notification-limit: 200  # Entity count limit per chunk

# Item Drop Monitoring Configuration
item:
  enabled: true
  interval: 60  # Detection interval in Ticks
  notification-limit: 100  # Item drop count limit per chunk

# Cooldown Configuration
cooldown:
  duration: 5  # Notification cooldown in minutes

# Notification Configuration
notification:
  broadcast: true  # Send notification to all players
  console: true  # Send notification to console
  show-coordinates: true  # Show coordinate range

# Message Configuration
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
