package tictim.tfts.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tictim.tfts.contents.entity.TFTSHook;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile{
	protected FishingHookMixin(EntityType<? extends Projectile> type, Level level){
		super(type, level);
	}

	@SuppressWarnings("ConstantValue")
	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"
			)
	)
	public boolean tfts$redirectFluidStateCheck(FluidState state, TagKey<Fluid> whatever){
		Object o = this;
		return o instanceof TFTSHook ? !state.isEmpty() : state.is(whatever);
	}
}
