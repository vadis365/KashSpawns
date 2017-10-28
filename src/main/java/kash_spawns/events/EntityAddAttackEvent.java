package kash_spawns.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// Test class for proof of concept

public class EntityAddAttackEvent {

	@SubscribeEvent
	public void onEntityJoinedWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityCreature && event.getEntity().isNonBoss() && !(event.getEntity() instanceof IMob)) {
			EntityCreature entity = (EntityCreature) event.getEntity();
			World world = entity.getEntityWorld();
			if (!world.isRemote) {
				
				// setting a generic damage attribute seems to spam the console - but it does seem to work
				setUpAttributes(entity);
				setUpAI(entity);
			}
		}
	}

	public void setUpAttributes(EntityCreature entity) {
		boolean setAttribute = true;
		if(entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
			setAttribute = false;
		if (setAttribute)
			entity.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	public void setUpAI(EntityCreature entity) {
		 for (Object taskObject : entity.tasks.taskEntries.toArray()) {
		 EntityAIBase ai = ((EntityAITaskEntry) taskObject).action;
		 if (ai instanceof EntityAIPanic)
			 entity.tasks.removeTask(ai);
		 }
			entity.tasks.addTask(1, new AIPassiveMobAttack(entity));

			// the boolean sets if other mobs of it's type help it (like pigmen)
			entity.targetTasks.addTask(1, new EntityAIHurtByTarget(entity, true, new Class[0]));
	}

	static class AIPassiveMobAttack extends EntityAIAttackMelee {
		public AIPassiveMobAttack(EntityCreature entity) {
			// may need to adjust the speed in here (depends on the entity)
			super(entity, 1.25D, true);
		}

		@Override
		protected void checkAndPerformAttack(EntityLivingBase entity, double distanceIn) {
			double reach = this.getAttackReachSqr(entity);
			if (distanceIn <= reach && this.attackTick <= 0) {
				attackTick = 20;
				attacker.swingArm(EnumHand.MAIN_HAND);

				// this overrides the vanilla method with out needing to fuck about with entities
				attackEntityAsMob(attacker, entity);
			}
		}

		public boolean attackEntityAsMob(EntityCreature entityAttacker, Entity entityIn) {
			// The damage below could be hard coded or set another way I guess maybe using an EntityDataManager object?
			// The generic damage value attribute spams the console and says it's ignored even though it works O.o
			boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(entityAttacker), (float) ((int) entityAttacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
			return flag;
		}
	}
}