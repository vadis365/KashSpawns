package kash_spawns.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kash_spawns.configs.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KashSpawnsMobEvent {
	@SubscribeEvent
	public void onMobKilled(LivingDeathEvent event) {
		if (event.getEntity() instanceof EntityLivingBase && event.getEntity().isNonBoss()) {
			EntityLivingBase entity = (EntityLivingBase) event.getEntity();
			World world = entity.getEntityWorld();
			if (!world.isRemote) {
				if(ConfigHandler.PLAYER_ONLY_SPAWNS && !(event.getSource().getEntity() instanceof EntityPlayer))
					return;
				if (entity instanceof IMob && world.rand.nextInt(ConfigHandler.MONSTER_SPAWN_PROBABILITY) == 0) {
					List<String> hostileMobList = new ArrayList<String>();
					for (int mobs = 0; mobs < ConfigHandler.HOSTILE_ENTITIES.length; mobs++) {
						String[] entry = ConfigHandler.HOSTILE_ENTITIES[mobs].trim().split(":");
						if (entry.length != 2)
							throw new IllegalArgumentException("Illegal entry found when reading KashSpawns config file for Hostile Mobs: " + ConfigHandler.HOSTILE_ENTITIES[mobs]);
						Integer weight = Integer.valueOf(entry[1]);
						for (int amount = 0; amount < weight; amount++)
							hostileMobList.add(entry[0]);
					}
					Collections.shuffle(hostileMobList);
					spawnMobfromList(world, entity, hostileMobList.get(0));
				}
				if (!(entity instanceof IMob) && world.rand.nextInt(ConfigHandler.CREATURE_SPAWN_PROBABILITY) == 0) {
					List<String> creatureMobList = new ArrayList<String>();
					for (int mobs = 0; mobs < ConfigHandler.NON_HOSTILE_ENTITIES.length; mobs++) {
						String[] entry = ConfigHandler.NON_HOSTILE_ENTITIES[mobs].trim().split(":");
						if (entry.length != 2)
							throw new IllegalArgumentException("Illegal entry found when reading KashSpawns config file for Non-Hostile Mobs: " + ConfigHandler.NON_HOSTILE_ENTITIES[mobs]);
						Integer weight = Integer.valueOf(entry[1]);
						for (int amount = 0; amount < weight; amount++)
							creatureMobList.add(entry[0]);
					}
					Collections.shuffle(creatureMobList);
					spawnMobfromList(world, entity, creatureMobList.get(0));
				}
			}
		}
	}

	private void spawnMobfromList(World world, EntityLivingBase originalEntity, String entityName) {
		if (!world.isRemote) {
			Entity entity = null;
			entity = EntityList.createEntityByIDFromName(new ResourceLocation(entityName), world);
			if (entity != null && entity instanceof EntityLivingBase) {
				EntityLiving entityliving = (EntityLiving) entity;
				entity.copyLocationAndAnglesFrom(originalEntity);
				entityliving.onInitialSpawn(world.getDifficultyForLocation(entity.getPosition()), (IEntityLivingData) null);
				world.spawnEntity(entity);
				entityliving.playLivingSound();
			}
		}
	}
}
