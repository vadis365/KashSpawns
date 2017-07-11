package kash_spawns;

import kash_spawns.configs.ConfigHandler;
import kash_spawns.events.KashSpawnsMobEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "kash_spawns", name = "kash_spawns", version = "0.3.0", guiFactory = "kash_spawns.configs.ConfigGuiFactory", dependencies = "after:*")

public class KashSpawns {

	@Instance("kash_spawns")
	public static KashSpawns INSTANCE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.INSTANCE.loadConfig(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(ConfigHandler.INSTANCE);
	}

	@EventHandler
	public void posInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new KashSpawnsMobEvent());
	}
}