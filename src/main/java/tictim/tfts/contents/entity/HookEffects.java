package tictim.tfts.contents.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

// why are all the particle spawning handled on client this is fucking cringe
public class HookEffects{
	public static void idleSplash(@NotNull TFTSHook hook, @NotNull ServerLevel level){
		float splashChance = 0.15F;
		// vanilla logic, looks fuckin weird idk
		// if(timeUntilLured<20) splashChance += (float)(20-timeUntilLured)*0.05F;
		// else if(timeUntilLured<40) splashChance += (float)(40-timeUntilLured)*0.02F;
		// else if(timeUntilLured<60) splashChance += (float)(60-timeUntilLured)*0.01F;

		if(hook.random().nextFloat()<splashChance){
			float angle = Mth.nextFloat(hook.random(), 0.0F, 360.0F)*((float)Math.PI/180F);
			float variation = Mth.nextFloat(hook.random(), 25.0F, 60.0F);
			double x = hook.getX()+(double)(Mth.sin(angle)*variation)*0.1D;
			double y = (float)Mth.floor(hook.getY())+1.0F;
			double z = hook.getZ()+(double)(Mth.cos(angle)*variation)*0.1D;
			BlockState state = level.getBlockState(BlockPos.containing(x, y-1.0D, z));
			if(state.is(Blocks.WATER)){
				level.sendParticles(ParticleTypes.SPLASH, x, y, z, 2+hook.random().nextInt(2),
						0.1F, 0.0D, 0.1F, 0.0D);
			}
		}
	}

	// definitely a real fish, trust me
	public static void fish(@NotNull TFTSHook hook,
	                        @NotNull ServerLevel level,
	                        double x, double y, double z,
	                        float fishAngle, double speed){
		if(!Float.isFinite(fishAngle)){
			fishAngle = hook.random().nextFloat()*2*(float)Math.PI;
		}
		float fishAngleRad = fishAngle*((float)Math.PI/180f);
		float sinFishAngle = Mth.sin(fishAngleRad);
		float cosFishAngle = Mth.cos(fishAngleRad);
		FluidState state = level.getFluidState(BlockPos.containing(x, y-1, z));
		if(state.is(Fluids.WATER)){ // TODO lava particles/other fluid block support? idk man
			if(hook.random().nextFloat()<0.15f){
				level.sendParticles(ParticleTypes.BUBBLE, x, y-0.1, z, 1,
						sinFishAngle, 0.1, cosFishAngle, 0);
			}

			float particleZOff = sinFishAngle*0.04f;
			float particleXOff = cosFishAngle*0.04f;
			sendForcedParticles(hook, level, ParticleTypes.FISHING, x, y, z, 0,
					particleXOff, 0.01, -particleZOff, speed);
			sendForcedParticles(hook, level, ParticleTypes.FISHING, x, y, z, 0,
					-particleXOff, 0.01, particleZOff, speed);
		}
	}

	@SuppressWarnings("SameParameterValue")
	private static void sendForcedParticles(@NotNull TFTSHook hook, @NotNull ServerLevel level,
	                                        @NotNull ParticleOptions type, double x, double y, double z,
	                                        int count, double xDist, double yDist, double zDist, double speed){
		Player owner = hook.getPlayerOwner();
		ClientboundLevelParticlesPacket forcePacket = new ClientboundLevelParticlesPacket(type, true,
				x, y, z, (float)xDist, (float)yDist, (float)zDist, (float)speed, count);
		ClientboundLevelParticlesPacket normalPacket = null;

		for(ServerPlayer player : level.players()){
			if(player.level()==level&&player.blockPosition().closerToCenterThan(new Vec3(x, y, z), 32)){
				if(player==owner) player.connection.send(forcePacket);
				else{
					if(normalPacket==null) normalPacket = new ClientboundLevelParticlesPacket(type, false,
							x, y, z, (float)xDist, (float)yDist, (float)zDist, (float)speed, count);
					player.connection.send(normalPacket);
				}
			}
		}
	}

	public static void splash(@NotNull TFTSHook hook, @NotNull ServerLevel level){
		splash(hook, level, 0.2f); // vanilla value
	}

	public static void splash(@NotNull TFTSHook hook,
	                          @NotNull ServerLevel level,
	                          float speed){
		hook.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25f,
				1+(hook.random().nextFloat()-hook.random().nextFloat())*0.4f);
		int particleCount = (int)(1+hook.getBbWidth()*20);
		double y = hook.getY()+0.5;
		level.sendParticles(ParticleTypes.BUBBLE, hook.getX(), y, hook.getZ(), particleCount,
				hook.getBbWidth(), 0, hook.getBbWidth(), speed);
		level.sendParticles(ParticleTypes.FISHING, hook.getX(), y, hook.getZ(), particleCount,
				hook.getBbWidth(), 0, hook.getBbWidth(), speed);
	}
}
