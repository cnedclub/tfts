package tictim.tfts.contents.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.entity.TFTSHook;

import java.util.Objects;
import java.util.UUID;

public class TFTSFishingRodItem extends Item{
	public TFTSFishingRodItem(Properties p){
		super(p.stacksTo(1));
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand){
		// fishing rod code
		ItemStack stack = player.getItemInHand(hand);
		if(player.fishing!=null){
			if(!level.isClientSide){
				int i = player.fishing.retrieve(stack);
				stack.hurtAndBreak(i, player, p -> p.broadcastBreakEvent(hand));
				setHookID(stack, null);
			}

			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL,
					1.0F, 0.4F/(level.getRandom().nextFloat()*0.4F+0.8F));
			player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
		}else{
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
					0.5F, 0.4F/(level.getRandom().nextFloat()*0.4F+0.8F));
			if(!level.isClientSide){
				int luck = EnchantmentHelper.getFishingLuckBonus(stack);
				int speed = EnchantmentHelper.getFishingSpeedBonus(stack);
				TFTSHook hook = new TFTSHook(player, level, luck, speed);
				level.addFreshEntity(hook);
				setHookID(stack, hook.getUUID());
			}

			player.awardStat(Stats.ITEM_USED.get(this));
			player.gameEvent(GameEvent.ITEM_INTERACT_START);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getEnchantmentValue(){
		return 1;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction){
		return ToolActions.DEFAULT_FISHING_ROD_ACTIONS.contains(toolAction);
	}

	@Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged){
		if(slotChanged) return true;
		if(!ItemStack.isSameItem(oldStack, newStack)) return true;
		CompoundTag oldTag = oldStack.getTag();
		CompoundTag newTag = newStack.getTag();
		if(oldTag==newTag) return false;
		if(oldTag==null||newTag==null) return true;
		for(String key : oldTag.getAllKeys()){
			if(key.equals(KEY_HOOK)) continue;
			if(!Objects.equals(oldTag.get(KEY_HOOK), newTag.get(key))) return true;
		}
		return false;
	}

	private static final String KEY_HOOK = "TFTSHook";

	@Nullable
	public static UUID getHookID(@NotNull ItemStack stack){
		CompoundTag tag = stack.getTag();
		if(tag==null) return null;
		return tag.hasUUID(KEY_HOOK) ? tag.getUUID(KEY_HOOK) : null;
	}

	public static void setHookID(@NotNull ItemStack stack, @Nullable UUID uuid){
		CompoundTag tag = stack.getTag();
		if(tag==null) stack.setTag(tag = new CompoundTag());
		if(uuid!=null) tag.putUUID(KEY_HOOK, uuid);
		else tag.remove(KEY_HOOK);
	}
}
