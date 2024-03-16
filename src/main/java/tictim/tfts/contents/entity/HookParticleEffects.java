package tictim.tfts.contents.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// why are all the particle spawning handled on client this is fucking cringe
public class HookParticleEffects{
	public static void idleSplash(@NotNull TFTSHook hook, @NotNull RandomSource random, @NotNull ServerLevel level, int timeUntilLured){
		float splashChance = 0.15F;
		if(timeUntilLured<20) splashChance += (float)(20-timeUntilLured)*0.05F;
		else if(timeUntilLured<40) splashChance += (float)(40-timeUntilLured)*0.02F;
		else if(timeUntilLured<60) splashChance += (float)(60-timeUntilLured)*0.01F;

		if(random.nextFloat()<splashChance){
			float angle = Mth.nextFloat(random, 0.0F, 360.0F)*((float)Math.PI/180F);
			float variation = Mth.nextFloat(random, 25.0F, 60.0F);
			double x = hook.getX()+(double)(Mth.sin(angle)*variation)*0.1D;
			double y = (float)Mth.floor(hook.getY())+1.0F;
			double z = hook.getZ()+(double)(Mth.cos(angle)*variation)*0.1D;
			BlockState state = level.getBlockState(BlockPos.containing(x, y-1.0D, z));
			if(state.is(Blocks.WATER)){
				level.sendParticles(ParticleTypes.SPLASH, x, y, z, 2+random.nextInt(2),
						0.1F, 0.0D, 0.1F, 0.0D);
			}
		}
	}

	public static void fish(@NotNull TFTSHook hook, @NotNull RandomSource random, @NotNull ServerLevel level, float fishDistance, float fishAngle){
		float fishAngleRad = fishAngle*((float)Math.PI/180F);
		float sinFishAngle = Mth.sin(fishAngleRad);
		float cosFishAngle = Mth.cos(fishAngleRad);
		double x = hook.getX()+(double)(sinFishAngle*fishDistance);
		double y = (float)Mth.floor(hook.getY())+1.0F;
		double z = hook.getZ()+(double)(cosFishAngle*fishDistance);
		BlockState state = level.getBlockState(BlockPos.containing(x, y-1.0D, z));
		if(state.is(Blocks.WATER)){
			if(random.nextFloat()<0.15F){
				level.sendParticles(ParticleTypes.BUBBLE, x, y-(double)0.1F, z, 1,
						sinFishAngle, 0.1D, cosFishAngle, 0.0D);
			}

			float particleZOff = sinFishAngle*0.04F;
			float particleXOff = cosFishAngle*0.04F;
			level.sendParticles(ParticleTypes.FISHING, x, y, z, 0,
					particleXOff, 0.01D, -particleZOff, 1.0D);
			level.sendParticles(ParticleTypes.FISHING, x, y, z, 0,
					-particleXOff, 0.01D, particleZOff, 1.0D);
		}
	}

	public static void splash(@NotNull TFTSHook hook, @NotNull RandomSource random, @NotNull ServerLevel level){
		hook.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F,
				1.0F+(random.nextFloat()-random.nextFloat())*0.4F);
		double y = hook.getY()+0.5D;
		level.sendParticles(ParticleTypes.BUBBLE, hook.getX(), y, hook.getZ(),
				(int)(1.0F+hook.getBbWidth()*20.0F), hook.getBbWidth(), 0.0D, hook.getBbWidth(),
				0.2F);
		level.sendParticles(ParticleTypes.FISHING, hook.getX(), y, hook.getZ(),
				(int)(1.0F+hook.getBbWidth()*20.0F), hook.getBbWidth(), 0.0D, hook.getBbWidth(),
				0.2F);
	}
}
