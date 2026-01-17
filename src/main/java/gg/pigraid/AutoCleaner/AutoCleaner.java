package gg.pigraid.AutoCleaner;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;

import java.util.Map;

/**
 * AutoCleaner - Periodically cleans up entities to improve server performance
 * Ported to NukkitPetteriM1Edition
 */
public class AutoCleaner extends PluginBase {
    private int repeatTime;
    private boolean deleteMobs;
    private boolean deleteAnimals;
    private boolean deleteProjectile;
    private boolean deleteExpOrb;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Load config
        deleteMobs = getConfig().getBoolean("deleteMobs");
        deleteAnimals = getConfig().getBoolean("deleteAnimals");
        deleteProjectile = getConfig().getBoolean("deleteProjectile");
        deleteExpOrb = getConfig().getBoolean("deleteExpOrb");
        repeatTime = getConfig().getInt("interval") * 20; // Convert seconds to ticks

        // Schedule cleanup task
        getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
            cleanupEntities();
        }, repeatTime, repeatTime);
    }

    /**
     * Cleanup entities across all levels
     */
    private void cleanupEntities() {
        Server server = getServer();
        Map<Integer, Level> levels = server.getLevels();

        for (Level level : levels.values()) {
            // Run chunk garbage collection
            level.doChunkGarbageCollection();
            level.unloadChunks(true);

            // Remove entities based on config
            Entity[] entities = level.getEntities();
            for (Entity entity : entities) {
                boolean shouldRemove = false;

                // Always remove items
                if (entity instanceof EntityItem) {
                    shouldRemove = true;
                }
                // Remove mobs if enabled
                else if (entity instanceof EntityMob && deleteMobs) {
                    shouldRemove = true;
                }
                // Remove animals if enabled
                else if (entity instanceof EntityAnimal && deleteAnimals) {
                    shouldRemove = true;
                }
                // Remove projectiles if enabled
                else if (entity instanceof EntityProjectile && deleteProjectile) {
                    shouldRemove = true;
                }
                // Remove XP orbs if enabled
                else if (entity instanceof EntityXPOrb && deleteExpOrb) {
                    shouldRemove = true;
                }

                if (shouldRemove) {
                    level.removeEntity(entity);
                }
            }
        }
    }

    // Getters and setters
    public int getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public boolean isDeleteMobs() {
        return deleteMobs;
    }

    public void setDeleteMobs(boolean deleteMobs) {
        this.deleteMobs = deleteMobs;
    }

    public boolean isDeleteAnimals() {
        return deleteAnimals;
    }

    public void setDeleteAnimals(boolean deleteAnimals) {
        this.deleteAnimals = deleteAnimals;
    }

    public boolean isDeleteProjectile() {
        return deleteProjectile;
    }

    public void setDeleteProjectile(boolean deleteProjectile) {
        this.deleteProjectile = deleteProjectile;
    }

    public boolean isDeleteExpOrb() {
        return deleteExpOrb;
    }

    public void setDeleteExpOrb(boolean deleteExpOrb) {
        this.deleteExpOrb = deleteExpOrb;
    }
}
