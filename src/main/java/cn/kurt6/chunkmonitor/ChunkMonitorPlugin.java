package cn.kurt6.chunkmonitor;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;

public class ChunkMonitorPlugin extends JavaPlugin {

    private Map<String, Long> chunkCooldown = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private boolean isFolia = false;
    private String language = "zh_CN";
    private Map<String, Map<String, String>> messages = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        detectServerType();
        loadLanguage();

        scheduler = Executors.newScheduledThreadPool(3);

        String msg = getMessage("enabled_message");
        getLogger().info(msg);

        String typeMsg = getMessage("server_type_message")
                .replace("%type%", isFolia ? "Folia" : "Paper/Spigot");
        getLogger().info(typeMsg);

        startTasks();
    }

    @Override
    public void onDisable() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        getLogger().info(getMessage("disabled_message"));
    }

    private void detectServerType() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
    }

    private void loadLanguage() {
        language = getConfig().getString("language", "zh_CN");
        ConfigurationSection messageSection = getConfig().getConfigurationSection("messages." + language);

        if (messageSection != null) {
            Map<String, String> langMessages = new ConcurrentHashMap<>();
            for (String key : messageSection.getKeys(false)) {
                langMessages.put(key, messageSection.getString(key, ""));
            }
            messages.put(language, langMessages);
        }
    }

    private String getMessage(String key) {
        Map<String, String> langMessages = messages.get(language);
        if (langMessages == null) {
            return "[" + key + "]";
        }
        return langMessages.getOrDefault(key, "[" + key + "]");
    }

    private void startTasks() {
        if (getConfig().getBoolean("mspt.enabled")) {
            long interval = getConfig().getLong("mspt.interval") * 50;
            scheduler.scheduleAtFixedRate(this::checkMspt, interval, interval, TimeUnit.MILLISECONDS);
        }

        if (getConfig().getBoolean("entity.enabled")) {
            long interval = getConfig().getLong("entity.interval") * 50;
            scheduler.scheduleAtFixedRate(this::checkEntity, interval, interval, TimeUnit.MILLISECONDS);
        }

        if (getConfig().getBoolean("item.enabled")) {
            long interval = getConfig().getLong("item.interval") * 50;
            scheduler.scheduleAtFixedRate(this::checkItem, interval, interval, TimeUnit.MILLISECONDS);
        }
    }

    private void checkMspt() {
        try {
            double limit = getConfig().getDouble("mspt.notification-limit");

            for (World world : Bukkit.getWorlds()) {
                Chunk[] chunks = world.getLoadedChunks();
                for (Chunk chunk : chunks) {
                    String chunkKey = getChunkKey(chunk);
                    if (isInCooldown(chunkKey)) continue;

                    try {
                        double mspt = calculateChunkLoad(chunk);
                        if (mspt > limit) {
                            notifyChunk(chunk, "mspt_alert", String.format("%.2f", mspt), limit);
                            setCooldown(chunkKey);
                        }
                    } catch (Exception e) {
                        // 静默处理
                    }
                }
            }
        } catch (Exception e) {
            getLogger().fine("MSPT检测异常");
        }
    }

    private void checkEntity() {
        try {
            int limit = getConfig().getInt("entity.notification-limit");

            for (World world : Bukkit.getWorlds()) {
                Chunk[] chunks = world.getLoadedChunks();
                for (Chunk chunk : chunks) {
                    String chunkKey = getChunkKey(chunk);
                    if (isInCooldown(chunkKey)) continue;

                    int entityCount = chunk.getEntities().length;
                    if (entityCount > limit) {
                        notifyChunk(chunk, "entity_alert", entityCount, limit);
                        setCooldown(chunkKey);
                    }
                }
            }
        } catch (Exception e) {
            getLogger().fine("实体检测异常");
        }
    }

    private void checkItem() {
        try {
            int limit = getConfig().getInt("item.notification-limit");

            for (World world : Bukkit.getWorlds()) {
                Chunk[] chunks = world.getLoadedChunks();
                for (Chunk chunk : chunks) {
                    String chunkKey = getChunkKey(chunk);
                    if (isInCooldown(chunkKey)) continue;

                    long itemCount = 0;
                    for (Entity entity : chunk.getEntities()) {
                        if (entity instanceof Item) {
                            itemCount++;
                        }
                    }

                    if (itemCount > limit) {
                        notifyChunk(chunk, "item_alert", itemCount, limit);
                        setCooldown(chunkKey);
                    }
                }
            }
        } catch (Exception e) {
            getLogger().fine("掉落物检测异常");
        }
    }

    private double calculateChunkLoad(Chunk chunk) {
        Entity[] entities = chunk.getEntities();
        double load = entities.length * 0.5;

        for (Entity entity : entities) {
            if (entity instanceof Item) {
                load += 0.1;
            } else {
                load += 0.3;
            }
        }

        return Math.min(load, 100.0);
    }

    private String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    private boolean isInCooldown(String chunkKey) {
        Long cooldownTime = chunkCooldown.get(chunkKey);
        if (cooldownTime == null) return false;

        if (System.currentTimeMillis() > cooldownTime) {
            chunkCooldown.remove(chunkKey);
            return false;
        }
        return true;
    }

    private void setCooldown(String chunkKey) {
        long cooldownMs = getConfig().getLong("cooldown.duration") * 60 * 1000;
        chunkCooldown.put(chunkKey, System.currentTimeMillis() + cooldownMs);
    }

    private void notifyChunk(Chunk chunk, String messageKey, Object value, Object limit) {
        String template = getMessage(messageKey);

        String world = chunk.getWorld().getName();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // 计算坐标范围 (每个区块16x16方块)
        int coordMinX = chunkX * 16;
        int coordMaxX = coordMinX + 15;
        int coordMinZ = chunkZ * 16;
        int coordMaxZ = coordMinZ + 15;

        String message = template
                .replace("%world%", world)
                .replace("%chunk_x%", String.valueOf(chunkX))
                .replace("%chunk_z%", String.valueOf(chunkZ))
                .replace("%coord_min_x%", String.valueOf(coordMinX))
                .replace("%coord_max_x%", String.valueOf(coordMaxX))
                .replace("%coord_min_z%", String.valueOf(coordMinZ))
                .replace("%coord_max_z%", String.valueOf(coordMaxZ))
                .replace("%value%", String.valueOf(value))
                .replace("%limit%", String.valueOf(limit));

        if (getConfig().getBoolean("notification.broadcast")) {
            try {
                // 只发送给在线玩家，不输出到控制台
                for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(message);
                }
            } catch (Exception e) {
                getLogger().fine("广播失败");
            }
        }

        if (getConfig().getBoolean("notification.console")) {
            getLogger().warning(message);
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        if (!getDataFolder().toPath().resolve("config.yml").toFile().exists()) {
            saveResource("config.yml", false);
        }
    }
}