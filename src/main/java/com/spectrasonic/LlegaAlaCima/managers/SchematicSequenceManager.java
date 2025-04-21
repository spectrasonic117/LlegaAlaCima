package com.spectrasonic.LlegaAlaCima.managers;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SchematicSequenceManager {

    private final JavaPlugin plugin;
    private final String emptySchematic;
    private final Location pastePivot;

    // Ahora no es final; se rellena al arrancar cada ronda
    private List<String> schematicSequence;
    private int currentIndex;
    private BukkitTask task;

    public SchematicSequenceManager(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        List<String> emptyList = config.getStringList("schematic_empty");
        this.emptySchematic = emptyList.isEmpty() ? null : emptyList.get(0);
        this.pastePivot = new Location(
                plugin.getServer().getWorlds().get(0),
                config.getDouble("paste_pivot.x"),
                config.getDouble("paste_pivot.y"),
                config.getDouble("paste_pivot.z"));
    }

    /** Arranca la secuencia de la ronda indicada (ej. 1,2,3…) */
    public void startSequence(int round) {
        schematicSequence = plugin.getConfig()
                .getStringList("schematic_list_secuency_round_" + round);
        if (schematicSequence.isEmpty()) {
            plugin.getLogger().warning(
                    "No hay schematics definidos para la ronda " + round);
            return;
        }

        // Determine interval based on round
        long intervalTicks;
        switch (round) {
            case 2:
                intervalTicks = 40L; // 2 seconds (2 * 20 ticks/sec)
                break;
            case 3:
                intervalTicks = 20L; // 1 second (1 * 20 ticks/sec)
                break;
            case 1: // Fallthrough for default case
            default:
                intervalTicks = 60L; // 3 seconds (3 * 20 ticks/sec)
                if (round != 1) {
                    plugin.getLogger().warning("Intervalo no definido para ronda " + round
                            + ". Usando intervalo por defecto (3 segundos).");
                }
                break;
        }

        stopSequence(); // cancela tarea previa
        currentIndex = 0;
        task = plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                () -> {
                    if (schematicSequence.isEmpty()) { // Check again in case config was reloaded empty
                        stopSequence();
                        return;
                    }
                    String name = schematicSequence.get(currentIndex);
                    // Asumimos que el método pasteSchematic sustituye bloques
                    SchematicUtils.pasteSchematic(plugin, name, pastePivot);
                    currentIndex = (currentIndex + 1) % schematicSequence.size();
                },
                0L, // Start immediately
                intervalTicks // Use the calculated interval
        );
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
