package com.spectrasonic.LlegaAlaCima.managers;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SchematicSequenceManager {
    private final JavaPlugin plugin;
    private final List<String> schematicSequence;
    private final String emptySchematic;
    private final Location pastePivot;
    private int currentIndex;
    private BukkitTask task;

    public SchematicSequenceManager(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.schematicSequence = config.getStringList("schematic_list_secuency");
        List<String> emptyList = config.getStringList("schematic_empty");
        this.emptySchematic = emptyList.isEmpty() ? null : emptyList.get(0);
        this.pastePivot = new Location(
            plugin.getServer().getWorlds().get(0),
            config.getConfigurationSection("paste_pivot").getDouble("x"),
            config.getConfigurationSection("paste_pivot").getDouble("y"),
            config.getConfigurationSection("paste_pivot").getDouble("z")
        );
        this.currentIndex = 0;
    }

    public void startSequence() {
        if (schematicSequence.isEmpty()) {
            plugin.getLogger().warning("No hay schematics definidos en la secuencia de la configuración.");
            return;
        }
        stopSequence(); // Cancela una tarea previa si existe
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            String schematicName = schematicSequence.get(currentIndex);
            SchematicUtils.pasteSchematic(plugin, schematicName, pastePivot);
            currentIndex = (currentIndex + 1) % schematicSequence.size();
        }, 0L, 80L);
    }

    public void stopSequence() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (emptySchematic != null) {
            SchematicUtils.pasteSchematic(plugin, emptySchematic, pastePivot);
        }
    }
}

