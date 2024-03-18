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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;
import tictim.tfts.angling.AnglingEnvironment;
import tictim.tfts.angling.AnglingUtils;
import tictim.tfts.angling.NibbleBehavior;
import tictim.tfts.contents.TFTSEntities;
import tictim.tfts.contents.anglingentry.AnglingEntry;
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

	@NotNull
	RandomSource random(){
		return this.random;
	}

	// 95% just lifted from base class, only changed here and there
	// TODO probably better to mixin away FluidState#is(WATER) call with !isEmpty() call, if its not possible then uhh do js asm idk
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
				// this logic is so fucking sus lmao
				this.openWater = this.anglingEntry==null||
						this.openWater&&this.outOfWaterTime<10&&this.calculateOpenWater(pos);

				if(inFluid){
					this.outOfWaterTime = Math.max(0, this.outOfWaterTime-1);
					if(isBiting()){
						this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1D*(double)this.syncronizedRandom.nextFloat()*(double)this.syncronizedRandom.nextFloat(), 0.0D));
					}

					if(!this.level().isClientSide)
						this.catchingTFTSFish(player, pos, fluidState);
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
					OpenWaterType.INSIDE_WATER : OpenWaterType.INVALID;
		}else return OpenWaterType.ABOVE_WATER;
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
		}else if(this.anglingEntry!=null&&isBiting()){ // TODO
			List<ItemStack> list = new ArrayList<>();
			this.anglingEntry.getLoot(list);

			event = new ItemFishedEvent(list, this.onGround() ? 2 : 1, this);
			MinecraftForge.EVENT_BUS.post(event);
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
				if(this.environment!=null) this.environment.processLoot(entity);
				this.level().addFreshEntity(entity);
				ExperienceOrb exp = new ExperienceOrb(player.level(),
						player.getX(), player.getY()+0.5D, player.getZ()+0.5D,
						this.random.nextInt(6)+1);
				if(this.environment!=null) this.environment.processExp(exp);
				player.level().addFreshEntity(exp);
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
				this.environment = AnglingUtils.getFluidEnvironment(this.environment, owner, pos, fluidState);
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
							}else{
								// fake bite TODO maybe occasional fake sounds, and chance to still hook the fish even
								//                when the hook is retrieved on fake bites
								Vec3 d = this.getDeltaMovement();
								this.setDeltaMovement(d.x+Mth.nextFloat(this.syncronizedRandom, -0.2f, 0.2f),
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

			if(this.fish!=null) this.fish.updatePosition(this, level);
		}else{ // biting
			if(--this.counter<=0) resetStates();
			if(this.fish!=null) this.fish.updatePosition(this, level);
		} // TODO fishing minigame
	}

	private void resetStates(){
		this.counter = 0;
		this.environment = null;
		this.fishArrived = false;
		this.anglingEntry = null;

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
