package tictim.tfts.contents.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CookingMortarItem extends Item{
	public CookingMortarItem(Properties p){
		super(p);
	}

	@Override @NotNull public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		ItemStack otherItem = player.getItemInHand(opposite(hand));
		if(canProcess(otherItem)){
			player.startUsingItem(hand);
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remaining){
		if(level.isClientSide) return;
		ItemStack otherItem = entity.getItemInHand(opposite(entity.getUsedItemHand()));
		if(!canProcess(otherItem)) entity.stopUsingItem();
	}

	@Override @NotNull public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity){
		if(level.isClientSide||!(entity instanceof Player player)) return stack;
		InteractionHand hand = entity.getUsedItemHand();
		ItemStack otherItem = entity.getItemInHand(opposite(hand));
		if(!canProcess(otherItem)) return stack;
		otherItem.shrink(1);
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Thing.STARCH));
		stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
		return stack.isEmpty() ? ItemStack.EMPTY : stack;
	}

	@Override @NotNull public UseAnim getUseAnimation(@NotNull ItemStack stack){
		return UseAnim.EAT;
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

	private static boolean canProcess(@NotNull ItemStack stack){ // TODO recipe based implementation
		Item item = stack.getItem();
		return item==Items.POTATO||item==Items.WHEAT;
	}

	private static InteractionHand opposite(InteractionHand hand){
		return hand==InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}
}
