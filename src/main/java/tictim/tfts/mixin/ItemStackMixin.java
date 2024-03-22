package tictim.tfts.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.BaitStat;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin{ // i'm using mixin because forge's tooltip event sucks absolute balls
	@Inject(
			method = "getTooltipLines",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
					shift = Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	public void tfts$extraTooltip(@Nullable Player player, TooltipFlag flags, CallbackInfoReturnable<?> i, List<Component> list){
		if(this.isEmpty()) return;
		LocalPlayer localPlayer = Minecraft.getInstance().player;
		if(localPlayer==null) return;
		var reg = localPlayer.connection.registryAccess().registry(TFTSRegistries.BAIT_STAT_REGISTRY_KEY);
		if(reg.isEmpty()) return;
		Registry<BaitStat> baitStats = reg.get();
		BaitStat stat = baitStats.get(ForgeRegistries.ITEMS.getKey(this.getItem()));
		if(stat==null||stat.rawStats().isEmpty()) return;

		list.add(Component.translatable("item.tfts.generic.tooltip.bait_stats"));

		boolean debug = flags.isAdvanced()&&Screen.hasShiftDown();

		for(var e : (debug ? stat.allStats() : stat.allStatsSorted()).object2DoubleEntrySet()){
			String key = e.getKey().getTranslationKey();
			if(I18n.exists(key)){
				if(debug){

					list.add(Component.translatable("item.tfts.generic.tooltip.bait_stat.debug",
							Component.translatable(key).withStyle(ChatFormatting.YELLOW),
							e.getKey().toString(),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e.getDoubleValue())));
				}else{
					list.add(Component.translatable("item.tfts.generic.tooltip.bait_stat",
							Component.translatable(key).withStyle(ChatFormatting.YELLOW),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e.getDoubleValue())));
				}
			}else if(debug){
				list.add(Component.translatable("item.tfts.generic.tooltip.bait_stat.debug_no_translation",
						e.getKey().toString(),
						ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e.getDoubleValue())));
			}
		}
	}

	@Shadow public abstract boolean isEmpty();
	@Shadow public abstract Item getItem();
}
