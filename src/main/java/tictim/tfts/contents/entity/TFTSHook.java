package tictim.tfts.contents.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;
import tictim.tfts.angling.AnglingEnvironment;
import tictim.tfts.angling.AnglingUtils;
import tictim.tfts.contents.TFTSEntities;
import tictim.tfts.contents.anglingentry.AnglingEntry;
import tictim.tfts.contents.item.TFTSFishingRodItem;

import java.util.Collections;
import java.util.List;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

public class TFTSHook extends FishingHook{
	private static final int ENV_UPDATE_DELAY = 20;

	@Nullable
	private MutableBlockPos environmentPos;
	@Nullable
	private AnglingEnvironment environment;

	private int environmentUpdateTickCount;

	protected int timeUntilLured;

	@Nullable
	protected AnglingEntry<?> entry;

	protected float fishAngle;

	public TFTSHook(EntityType<? extends TFTSHook> entityType, Level level){
		super(entityType, level);
	}

	@SuppressWarnings("SuspiciousNameCombination") // shut the fuck up
	public TFTSHook(Player player, Level level, int luck, int lureSpeed){
		super(TFTSEntities.HOOK.get(), level, luck, lureSpeed);
		this.setOwner(player);
		float xRot = player.getXRot();
		float yRot = player.getYRot();
		float something = Mth.cos(-yRot*((float)Math.PI/180F)-(float)Math.PI);
		float alsoSomething = Mth.sin(-yRot*((float)Math.PI/180F)-(float)Math.PI);
		float someAngleShit = -Mth.cos(-xRot*((float)Math.PI/180F));
		float idk = Mth.sin(-xRot*((float)Math.PI/180F));
		double x = player.getX()-(double)alsoSomething*0.3D;
		double y = player.getEyeY();
		double z = player.getZ()-(double)something*0.3D;
		this.moveTo(x, y, z, yRot, xRot);
		Vec3 vec = new Vec3(-alsoSomething, Mth.clamp(-(idk/someAngleShit), -5.0F, 5.0F), -something);
		double mag = vec.length();
		vec = vec.multiply(0.6D/mag+this.random.triangle(0.5D, 0.0103365D),
				0.6D/mag+this.random.triangle(0.5D, 0.0103365D),
				0.6D/mag+this.random.triangle(0.5D, 0.0103365D));
		this.setDeltaMovement(vec);
		this.setYRot((float)(Mth.atan2(vec.x, vec.z)*(double)(180F/(float)Math.PI)));
		this.setXRot((float)(Mth.atan2(vec.y, vec.horizontalDistance())*(double)(180F/(float)Math.PI)));
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
	}

	// TODO override shouldStopFishing or something
	// 95% just lifted from base class, only changed here and there
	public void tick(){
		this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits()^this.level().getGameTime());

		// Projectile#tick() {
		if(!this.hasBeenShot){
			this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
			this.hasBeenShot = true;
		}

		if(!this.leftOwner) this.leftOwner = this.checkLeftOwner();
		// }

		Player player = this.getPlayerOwner();
		if(player==null){
			this.discard();
			return;
		}
		if(!this.level().isClientSide&&this.shouldStopFishing(player)) return;

		if(this.onGround()){
			++this.life;
			if(this.life>=1200){
				this.discard();
				return;
			}
		}else this.life = 0;

		float fluidHeight = 0.0F;
		BlockPos pos = this.blockPosition();
		FluidState fluidState = this.level().getFluidState(pos);
		if(!fluidState.isEmpty()){
			fluidHeight = fluidState.getHeight(this.level(), pos);
		}

		boolean inFluid = fluidHeight>0.0F;
		if(this.currentState==FishHookState.FLYING){
			if(this.hookedIn!=null){
				this.setDeltaMovement(Vec3.ZERO);
				this.currentState = FishHookState.HOOKED_IN_ENTITY;
				return;
			}

			if(inFluid){
				this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
				this.currentState = FishHookState.BOBBING;
				return;
			}

			this.checkCollision();
		}else{
			if(this.currentState==FishHookState.HOOKED_IN_ENTITY){
				if(this.hookedIn!=null){
					if(!this.hookedIn.isRemoved()&&this.hookedIn.level().dimension()==this.level().dimension()){
						this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8D), this.hookedIn.getZ());
					}else{
						this.setHookedEntity(null);
						this.currentState = FishHookState.FLYING;
					}
				}

				return;
			}

			if(this.currentState==FishHookState.BOBBING){
				Vec3 vec3 = this.getDeltaMovement();
				double d0 = this.getY()+vec3.y-(double)pos.getY()-(double)fluidHeight;
				if(Math.abs(d0)<0.01D){
					d0 += Math.signum(d0)*0.1D;
				}

				this.setDeltaMovement(vec3.x*0.9D, vec3.y-d0*(double)this.random.nextFloat()*0.2D, vec3.z*0.9D);
				this.openWater = this.nibble<=0&&this.timeUntilHooked<=0||
						this.openWater&&this.outOfWaterTime<10&&this.calculateOpenWater(pos);

				if(inFluid){
					this.outOfWaterTime = Math.max(0, this.outOfWaterTime-1);
					if(this.biting){
						this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1D*(double)this.syncronizedRandom.nextFloat()*(double)this.syncronizedRandom.nextFloat(), 0.0D));
					}

					if(!this.level().isClientSide){
						BlockPos above = pos.above();
						BlockState aboveBlockState = this.level().getBlockState(above);
						FluidState aboveFluidState = this.level().getFluidState(above);
						if(aboveFluidState.isEmpty()&&aboveBlockState.getCollisionShape(this.level(), pos).isEmpty())
							this.catchingTFTSFish(player, pos, fluidState);
					}
				}else{
					this.outOfWaterTime = Math.min(10, this.outOfWaterTime+1);
				}
			}
		}

		if(fluidState.isEmpty()){
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
		}

		this.move(MoverType.SELF, this.getDeltaMovement());
		this.updateRotation();
		if(this.currentState==FishHookState.FLYING&&(this.onGround()||this.horizontalCollision)){
			this.setDeltaMovement(Vec3.ZERO);
		}

		this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
		this.reapplyPosition();
	}

	@Override
	@NotNull
	protected OpenWaterType getOpenWaterTypeForBlock(@NotNull BlockPos pos){
		BlockState state = this.level().getBlockState(pos);
		if(!state.isAir()&&!state.is(Blocks.LILY_PAD)){
			FluidState fluid = state.getFluidState();
			return !fluid.isEmpty()&&
					fluid.isSource()&&
					state.getCollisionShape(this.level(), pos).isEmpty() ?
					FishingHook.OpenWaterType.INSIDE_WATER : FishingHook.OpenWaterType.INVALID;
		}else{
			return FishingHook.OpenWaterType.ABOVE_WATER;
		}
	}

	@Override
	public int retrieve(@NotNull ItemStack stack){
		Player player = this.getPlayerOwner();
		if(this.level().isClientSide||player==null||this.shouldStopFishing(player)) return 0;

		int itemDamage = 0;
		ItemFishedEvent event = null;
		Entity hookedIn = this.getHookedIn();
		if(hookedIn!=null){
			this.pullEntity(hookedIn);
			CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, stack, this, Collections.emptyList());
			this.level().broadcastEntityEvent(this, (byte)31);
			itemDamage = hookedIn instanceof ItemEntity ? 3 : 5;
		}else if(this.nibble>0){
			ServerLevel serverLevel = (ServerLevel)this.level();
			LootParams lootParams = (new LootParams.Builder(serverLevel))
					.withParameter(LootContextParams.ORIGIN, this.position())
					.withParameter(LootContextParams.TOOL, stack)
					.withParameter(LootContextParams.THIS_ENTITY, this)
					.withParameter(LootContextParams.KILLER_ENTITY, player)
					.withParameter(LootContextParams.THIS_ENTITY, this)
					.withLuck((float)this.luck+player.getLuck()).create(LootContextParamSets.FISHING);
			LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
			List<ItemStack> list = lootTable.getRandomItems(lootParams);
			event = new ItemFishedEvent(list, this.onGround() ? 2 : 1, this);
			EVENT_BUS.post(event);
			if(event.isCanceled()){
				this.discard();
				return event.getRodDamage();
			}
			CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, stack, this, list);

			for(ItemStack drop : list){
				ItemEntity entity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), drop);
				double x = player.getX()-this.getX();
				double y = player.getY()-this.getY();
				double z = player.getZ()-this.getZ();
				entity.setDeltaMovement(x*0.1D, y*0.1D+Math.sqrt(Math.sqrt(x*x+y*y+z*z))*0.08D, z*0.1D);
				this.level().addFreshEntity(entity);
				player.level().addFreshEntity(new ExperienceOrb(player.level(),
						player.getX(), player.getY()+0.5D, player.getZ()+0.5D,
						this.random.nextInt(6)+1));
				if(drop.is(ItemTags.FISHES)){
					player.awardStat(Stats.FISH_CAUGHT, 1);
				}
			}

			itemDamage = 1;
		}

		if(this.onGround()) itemDamage = 2;

		this.discard();
		return event==null ? itemDamage : event.getRodDamage();
	}

	// serves same purpose base class's catchingFish (manages server-side states) but with this mod's fishing content
	private void catchingTFTSFish(@NotNull Player owner, @NotNull BlockPos pos, @Nullable FluidState fluidState){
		ServerLevel level = (ServerLevel)this.level();
		int ticks = 1;

		// Disabled vanilla's raining/sky visible modifier
		// BlockPos above = pos.above();
		// if(this.random.nextFloat()<0.25F&&this.level().isRainingAt(above)) ++ticks;
		// if(this.random.nextFloat()<0.5F&&!this.level().canSeeSky(above)) --ticks;

		if(this.environmentPos==null||!this.environmentPos.equals(pos)){ // TODO need to limit environment re-calculation on initial state
			if(this.environmentUpdateTickCount++<ENV_UPDATE_DELAY) return;
			this.environmentUpdateTickCount = 0;
			if(this.environmentPos==null) this.environmentPos = new MutableBlockPos();
			this.environmentPos.set(pos);
			this.environment = AnglingUtils.getFluidEnvironment(this.environment, owner, pos, fluidState);
		}

		AnglingEnvironment env = this.environment;
		if(env==null) return;

		// TODO rewrite this shit
		//      better to declare new fields than reuse base fields, to prevent unwanted state pollution

		if(this.nibble>0){
			--this.nibble;
			if(this.nibble<=0){
				this.timeUntilLured = 0;
				this.timeUntilHooked = 0;
				this.getEntityData().set(DATA_BITING, false);
				TFTSMod.LOGGER.info("biting := false");
			}
		}else if(this.timeUntilHooked>0){
			this.timeUntilHooked -= ticks;
			if(this.timeUntilHooked>0){
				this.fishAngle += (float)this.random.triangle(0.0D, 9.188D);
				HookParticleEffects.fish(this, this.random, level, this.timeUntilHooked*.1f, this.fishAngle);
			}else{
				HookParticleEffects.splash(this, this.random, level);

				this.nibble = Mth.nextInt(this.random, 20, 40);
				this.getEntityData().set(DATA_BITING, true);
				TFTSMod.LOGGER.info("biting := true");
			}
		}else if(this.timeUntilLured>0){
			this.timeUntilLured -= ticks;
			HookParticleEffects.idleSplash(this, this.random, level, this.timeUntilLured);

			if(this.timeUntilLured<=0){
				this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
				this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
				TFTSMod.LOGGER.info("timeUntilHooked := {}", this.timeUntilHooked);
			}
		}else{
			this.timeUntilLured = Mth.nextInt(this.random, 100, 600)-this.lureSpeed*20*5;
			TFTSMod.LOGGER.info("timeUntilLured := {}", this.timeUntilLured);
		}
	}

	@Override
	protected boolean shouldStopFishing(Player player){
		if(!player.isRemoved()&&player.isAlive()&&this.distanceToSqr(player)<=1024.0D){
			ItemStack main = player.getMainHandItem();
			ItemStack off = player.getOffhandItem();
			if(isItemPointingToThisHook(main)||isItemPointingToThisHook(off)){
				return false;
			}
		}
		this.discard();
		return true;
	}

	public boolean isItemPointingToThisHook(@NotNull ItemStack stack){
		return this.getUUID().equals(TFTSFishingRodItem.getHookID(stack));
	}
}
