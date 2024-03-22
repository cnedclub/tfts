package tictim.tfts.contents.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.TFTSEntities;
import tictim.tfts.contents.fish.AnglingEntry;
import tictim.tfts.contents.fish.AnglingEnvironment;
import tictim.tfts.contents.fish.AnglingUtils;
import tictim.tfts.contents.fish.NibbleBehavior;
import tictim.tfts.contents.item.TFTSFishingRodItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TFTSHook extends FishingHook{
	private static final int ENV_UPDATE_DELAY = 20;
	private static final int NIBBLE_DELAY = 60;

	@Nullable private MutableBlockPos environmentPos;
	@Nullable private AnglingEnvironment environment;

	private int counter;
	private boolean fishArrived;

	@Nullable private AnglingEntry<?> anglingEntry;
	@Nullable private ImaginaryFish fish;

	public TFTSHook(EntityType<? extends TFTSHook> entityType, Level level){
		super(entityType, level);
	}

	@SuppressWarnings("SuspiciousNameCombination") // shut the fuck up
	public TFTSHook(Player player, Level level, int luck, int lureSpeed){
		super(TFTSEntities.HOOK.get(), level, luck, lureSpeed);
		setOwner(player);
		float xRot = player.getXRot();
		float yRot = player.getYRot();
		float something = Mth.cos(-yRot*((float)Math.PI/180F)-(float)Math.PI);
		float alsoSomething = Mth.sin(-yRot*((float)Math.PI/180F)-(float)Math.PI);
		float someAngleShit = -Mth.cos(-xRot*((float)Math.PI/180F));
		float idk = Mth.sin(-xRot*((float)Math.PI/180F));
		double x = player.getX()-(double)alsoSomething*0.3D;
		double y = player.getEyeY();
		double z = player.getZ()-(double)something*0.3D;
		moveTo(x, y, z, yRot, xRot);
		Vec3 vec = new Vec3(-alsoSomething, Mth.clamp(-(idk/someAngleShit), -5.0F, 5.0F), -something);
		double mag = vec.length();
		// what the fuck is this const value
		vec = vec.multiply(0.6/mag+this.random.triangle(0.5, 0.0103365),
				0.6/mag+this.random.triangle(0.5, 0.0103365),
				0.6/mag+this.random.triangle(0.5, 0.0103365));
		setDeltaMovement(vec);
		double rad2deg = 180/Math.PI;
		setYRot((float)(Mth.atan2(vec.x, vec.z)*rad2deg));
		setXRot((float)(Mth.atan2(vec.y, vec.horizontalDistance())*rad2deg));
		this.yRotO = getYRot();
		this.xRotO = getXRot();
	}

	@NotNull RandomSource random(){
		return this.random;
	}

	@Override public void tick(){
		super.tick();
		// 169% sure nobody would use this stupid thing but whatever
		if(this.currentState==FishHookState.BOBBING) this.openWater |= this.anglingEntry==null;
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
					OpenWaterType.INSIDE_WATER : OpenWaterType.INVALID;
		}else return OpenWaterType.ABOVE_WATER;
	}

	@Override
	public int retrieve(@NotNull ItemStack stack){
		Player player = getPlayerOwner();
		if(level().isClientSide||player==null||shouldStopFishing(player)) return 0;

		int itemDamage = 0;
		Entity hookedIn = getHookedIn();
		if(hookedIn!=null){
			pullEntity(hookedIn);
			CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, stack, this, Collections.emptyList());
			level().broadcastEntityEvent(this, (byte)31);
			discard();
			return hookedIn instanceof ItemEntity ? 3 : 5;
		}
		AnglingEntry<?> entry = this.anglingEntry;
		if(entry!=null&&(isBiting()||(this.fishArrived&&this.random.nextDouble()<.25))){
			List<ItemStack> list = new ArrayList<>();
			entry.getLoot(list);

			ItemFishedEvent event = new ItemFishedEvent(list, this.onGround() ? 2 : 1, this);
			MinecraftForge.EVENT_BUS.post(event);
			if(event.isCanceled()){
				discard();
				return event.getRodDamage();
			}
			CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, stack, this, list);

			for(ItemStack drop : list){
				ItemEntity entity = new ItemEntity(level(), getX(), getY(), getZ(), drop);
				double dx = player.getX()-getX();
				double dy = player.getY()-getY();
				double dz = player.getZ()-getZ();
				entity.setDeltaMovement(dx*.1, dy*.1+Math.sqrt(Math.sqrt(dx*dx+dy*dy+dz*dz))*.08, dz*.1);
				if(this.environment!=null) this.environment.processLoot(entity);
				this.level().addFreshEntity(entity);

				int exp = entry.getExperience(this.random);
				if(exp>0){
					ExperienceOrb expOrb = new ExperienceOrb(player.level(), player.getX(), player.getY()+.5, player.getZ()+.5, exp);
					if(this.environment!=null) this.environment.processExp(expOrb);
					player.level().addFreshEntity(expOrb);
				}

				if(drop.is(ItemTags.FISHES)){
					player.awardStat(Stats.FISH_CAUGHT, 1);
				}
			}

			discard();
			return event.getRodDamage();
		}

		if(onGround()) itemDamage = 2;
		discard();
		return itemDamage;
	}

	// this method name is a blatant lie, this m,tehod catches 0 motherucking fish
	@Override protected void catchingFish(@NotNull BlockPos pos){
		Player owner = getPlayerOwner();
		if(owner==null) return;
		ServerLevel level = (ServerLevel)this.level();
		FluidState fluidState = this.level().getFluidState(pos);

		if(this.anglingEntry==null){ // luring state
			BlockPos above = pos.above();
			BlockState aboveBlockState = this.level().getBlockState(above);
			FluidState aboveFluidState = this.level().getFluidState(above);
			if(!aboveFluidState.isEmpty()||!aboveBlockState.getCollisionShape(this.level(), pos).isEmpty()) return;

			if(this.environment==null||this.environmentPos==null||!this.environmentPos.equals(pos)){
				if(this.counter++<ENV_UPDATE_DELAY) return;
				this.counter = 0;
				if(this.environmentPos==null) this.environmentPos = new MutableBlockPos();
				this.environmentPos.set(pos);
				this.environment = AnglingUtils.getFluidEnvironment(this.environment, level, pos, fluidState);
			}

			if(this.counter<=0){
				//this.counter = Mth.nextInt(this.random, 100, 600)-this.lureSpeed*20*5; TODO test code
				this.counter = 10;
			}else{
				this.counter--;
				HookEffects.idleSplash(this, level);

				if(this.counter<=0){
					this.anglingEntry = AnglingUtils.pick(level, owner, pos, this.environment, this.random);
					TFTSMod.LOGGER.info("AnglingEntry: {}", this.anglingEntry);
				}
			}
		}else if(!isBiting()){ // nibbling state
			boolean bitTheHook = false;
			NibbleBehavior b = anglingEntry.nibbleBehavior();
			switch(b.type()){
				case NONE -> bitTheHook = true;
				case SNATCH -> {
					if(this.fish==null){
						this.fish = new ImaginaryFish();
						this.fish.initPosition(level, this);
					}
					if(this.fish.moveTo(this, level, ImaginaryFish.FAST_SPEED)){
						// TODO after implementing fishing minigame snatchers should immediately trigger the minigame
						bitTheHook = true;
					}
				}
				case NIBBLE -> {
					if(this.fish==null){
						this.fish = new ImaginaryFish();
						this.fish.initPosition(level, this);
					}
					if(!this.fishArrived){
						if(this.fish.moveTo(this, level, ImaginaryFish.NORMAL_SPEED)){
							this.fish.xRot = 0;
							this.fish.yRot = Mth.wrapDegrees(this.fish.yRot+180f+
									(float)this.random.triangle(0, 9.188)); // wack ass magic const
							this.fishArrived = true;
						}
					}else{
						if(++this.counter>=NIBBLE_DELAY){
							this.counter = 0;

							NibbleBehavior.Nibble nibble = (NibbleBehavior.Nibble)b;

							if(this.random.nextDouble()<=nibble.biteChance()){
								bitTheHook = true; // what an idiot!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

								Vec3 d = getDeltaMovement();
								setDeltaMovement(d.x+Mth.nextFloat(this.syncronizedRandom, -0.3f, 0.3f),
										d.y, d.z+Mth.nextFloat(this.syncronizedRandom, -0.3f, 0.3f));
							}else{
								// fake bite TODO maybe occasional fake sounds, and chance to still hook the fish even
								//                when the hook is retrieved on fake bites
								Vec3 d = getDeltaMovement();
								setDeltaMovement(d.x+Mth.nextFloat(this.syncronizedRandom, -0.2f, 0.2f),
										-0.2f*Mth.nextFloat(this.syncronizedRandom, 0.6f, 1),
										d.z+Mth.nextFloat(this.syncronizedRandom, -0.2f, 0.2f));
								TFTSMod.LOGGER.info("Nibble!");
							}
						}
						if(this.fish!=null) this.fish.setPosition(this.getX(), this.getY(), this.getZ());
					}
				}
			}

			if(bitTheHook){
				HookEffects.splash(this, level);
				this.counter = Mth.nextInt(this.random, 20, 40); // TODO?
				setBiting(true);
			}

			if(this.fish!=null) this.fish.processDelta(this, level);
		}else{ // biting
			if(this.fish!=null){
				this.fish.setPosition(this.getX(), this.getY(), this.getZ());
				this.fish.processDelta(this, level);
			}
			if(--this.counter<=0) resetStates();
		} // TODO fishing minigame
	}

	private void resetStates(){
		this.counter = 0;
		this.environment = null;
		this.fishArrived = false;
		this.anglingEntry = null;
		this.fish = null;

		setBiting(false);
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

	public boolean isBiting(){
		return this.biting;
	}

	private void setBiting(boolean biting){
		this.getEntityData().set(DATA_BITING, biting);
		TFTSMod.LOGGER.info("Biting: {}", biting);
	}
}
