package kash_spawns.configs;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {

	public static final ConfigHandler INSTANCE = new ConfigHandler();
	public Configuration CONFIG;
	public static String[] HOSTILE_ENTITIES;
	public static String[] NON_HOSTILE_ENTITIES;
	public static String[] usedCategories = { "Hostile Mobs that can spawn on mob death", "Non-Hostile Mobs that can spawn on mob death", "Player Settings"};
	public static int MONSTER_SPAWN_PROBABILITY;
	public static int CREATURE_SPAWN_PROBABILITY;
	public static boolean PLAYER_ONLY_SPAWNS;

	public void loadConfig(FMLPreInitializationEvent event) {
		CONFIG = new Configuration(event.getSuggestedConfigurationFile());
		CONFIG.load();
		syncConfigs();
	}

	private void syncConfigs() {
		CONFIG.addCustomCategoryComment("Hostile Mobs that can spawn on mob death", "Controls the Mob and Weight of spawns." );
		HOSTILE_ENTITIES = CONFIG.getStringList("Hostile Mobs", "Hostile Mobs that can spawn on mob death", new String[] { "Zombie:10" }, "Must Have a weight.");
		CONFIG.addCustomCategoryComment("Non-Hostile Mobs that can spawn on mob death", "Controls the Mob and Weight of spawns." );
		NON_HOSTILE_ENTITIES = CONFIG.getStringList("Non-Hostile Mobs", "Non-Hostile Mobs that can spawn on mob death", new String[] { "Sheep:10" }, "Must Have a weight.");
		MONSTER_SPAWN_PROBABILITY = CONFIG.get("Hostile Mobs that can spawn on mob death", "Chance a Mob Will be spawned (Lower numbers increase chance)", 5).getInt(5);
		CREATURE_SPAWN_PROBABILITY = CONFIG.get("Non-Hostile Mobs that can spawn on mob death", "Chance a Mob Will be spawned (Lower numbers increase chance)", 10).getInt(5);
		PLAYER_ONLY_SPAWNS = CONFIG.get("Player Settings", "Only Player Kills Cause Multi-Spawning", true, "").getBoolean(true);
		if (CONFIG.hasChanged())
			CONFIG.save();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals("kash_spawns"))
			syncConfigs();
	}
}
