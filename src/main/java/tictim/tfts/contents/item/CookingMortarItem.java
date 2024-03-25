package tictim.tfts.contents.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.recipe.GrindingContext;
import tictim.tfts.contents.recipe.GrindingRecipe;
import tictim.tfts.contents.recipe.InWorldRecipeProcessor;
import tictim.tfts.contents.recipe.RecipeResult;
import tictim.tfts.utils.A;

import java.util.List;
import java.util.function.Consumer;

public class CookingMortarItem extends Item{
	public CookingMortarItem(Properties p){
		super(p);
	}

	@Override @NotNull public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(player instanceof ServerPlayer sp&&canProcess(sp, opposite(hand))) player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	@Override public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remaining){
		InteractionHand hand = opposite(entity.getUsedItemHand());
		ItemStack otherItem = entity.getItemInHand(hand);
		if(otherItem.isEmpty()){
			entity.stopUsingItem();
			return;
		}
		if((remaining<=getUseDuration(stack)-7)&&remaining%4==0){
			triggerItemUseEffects(entity, otherItem, 5);
		}
	}

	private void triggerItemUseEffects(@NotNull LivingEntity entity, ItemStack stack, int amount){
		spawnItemParticles(entity, stack, amount);
		entity.playSound(getEatingSound(), 0.5F+0.5F*(float)entity.getRandom().nextInt(2),
				(entity.getRandom().nextFloat()-entity.getRandom().nextFloat())*0.2F+1.0F);
	}

	private void spawnItemParticles(@NotNull LivingEntity entity, ItemStack stack, int amount){
		final float deg2rad = (float)Math.PI/180;
		for(int i = 0; i<amount; ++i){
			Vec3 delta = new Vec3(((double)entity.getRandom().nextFloat()-.5)*.1, Math.random()*.1+.1, 0);
			delta = delta.xRot(-entity.getXRot()*deg2rad);
			delta = delta.yRot(-entity.getYRot()*deg2rad);
			double y = (double)(-entity.getRandom().nextFloat())*.6-.3;
			Vec3 pos = new Vec3(((double)entity.getRandom().nextFloat()-.5)*.3, y, .6);
			pos = pos.xRot(-entity.getXRot()*deg2rad);
			pos = pos.yRot(-entity.getYRot()*deg2rad);
			pos = pos.add(entity.getX(), entity.getEyeY(), entity.getZ());
			if(entity.level() instanceof ServerLevel serverLevel){ //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
				serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), pos.x, pos.y, pos.z, 1, delta.x, delta.y+.05, delta.z, 0);
			}else{
				entity.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), pos.x, pos.y, pos.z, delta.x, delta.y+.05, delta.z);
			}
		}
	}


	@Override @NotNull public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity){
		if(level.isClientSide||!(entity instanceof ServerPlayer player)) return stack;

		InteractionHand hand = opposite(entity.getUsedItemHand());
		ItemStack otherItem = entity.getItemInHand(hand);
		if(otherItem.isEmpty()) return stack;

		triggerItemUseEffects(entity, otherItem, 16);

		GrindingContext ctx = new GrindingContext(player, hand);
		var recipe = getRecipe(player.server, ctx);
		if(recipe==null) return stack;
		ItemStack result = recipe.process(ctx, new InWorldRecipeProcessor(player.serverLevel(), player.position()));
		player.setItemInHand(hand, result);
		stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
		return stack.isEmpty() ? ItemStack.EMPTY : stack;
	}

	@Override @NotNull public UseAnim getUseAnimation(@NotNull ItemStack stack){
		return UseAnim.CUSTOM;
	}
	@Override @NotNull public SoundEvent getEatingSound(){
		return SoundEvents.WOOD_HIT;
	}
	@Override public int getUseDuration(@NotNull ItemStack stack){
		return 100;
	}

	@Override public void appendHoverText(@NotNull ItemStack stack, @Nullable Level pLevel, @NotNull List<Component> text, @NotNull TooltipFlag flags){
		text.add(Component.translatable("item.tfts.cooking_mortar.tooltip.0"));
		text.add(Component.translatable("item.tfts.cooking_mortar.tooltip.1"));
	}

	@Override public void initializeClient(Consumer<IClientItemExtensions> consumer){
		consumer.accept(new IClientItemExtensions(){
			@Override public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
			                                                 ItemStack stack, float partialTick, float equipProcess,
			                                                 float swingProcess){
				if(player.isUsingItem()&&player.getUseItemRemainingTicks()>0&&getUsingArm(player)==arm){
					applyEatTransform(player, poseStack, partialTick, arm, stack);
					applyItemArmTransform(poseStack, arm, equipProcess);
					return true;
				}else return false;
			}

			private HumanoidArm getUsingArm(LocalPlayer player){
				return player.getUsedItemHand()==InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
			}

			private void applyEatTransform(LocalPlayer player, PoseStack poseStack, float partialTicks, HumanoidArm arm, ItemStack stack){
				float f = (float)player.getUseItemRemainingTicks()-partialTicks+1;
				float duration = f/(float)stack.getUseDuration();
				if(duration<0.8f){
					poseStack.translate(0, Mth.abs(Mth.cos(f/4*(float)Math.PI)*0.1f), 0);
				}

				float pow = 1-(float)Math.pow(duration, 27);
				int sign = arm==HumanoidArm.RIGHT ? 1 : -1;
				poseStack.translate(pow*0.6F*(float)sign, pow*-0.5F, pow*0);
				poseStack.mulPose(Axis.YP.rotationDegrees((float)sign*pow*90));
				poseStack.mulPose(Axis.XP.rotationDegrees(pow*10));
				poseStack.mulPose(Axis.ZP.rotationDegrees((float)sign*pow*30));
			}

			private void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equiProcess){
				int i = arm==HumanoidArm.RIGHT ? 1 : -1;
				poseStack.translate((float)i*0.56F, -0.52F+equiProcess*-0.6F, -0.72F);
			}
		});
	}

	private static boolean canProcess(@NotNull ServerPlayer player, @NotNull InteractionHand inputItemHand){
		return getRecipe(player.server, new GrindingContext(player, inputItemHand))!=null;
	}

	@Nullable private static RecipeResult<GrindingRecipe.Context, GrindingRecipe.ResultProcessor, ItemStack> getRecipe(
			@NotNull MinecraftServer server, @NotNull GrindingContext context){
		return A.getRecipe(server, GrindingRecipe.TYPE, context);
	}

	private static InteractionHand opposite(InteractionHand hand){
		return hand==InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}
}
