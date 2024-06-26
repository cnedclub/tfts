package tictim.tfts.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tictim.tfts.client.ClientUtils;
import tictim.tfts.contents.fish.AnglingUtils;
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
		BaitStat baitStat = AnglingUtils.getBaitStat(this.getItem(), localPlayer.connection.registryAccess());
		if(baitStat==null||baitStat.rawStats().isEmpty()) return;

		list.add(Component.translatable("item.tfts.generic.tooltip.bait_stats"));
		ClientUtils.addBaitStatText(list, baitStat, flags.isAdvanced()&&Screen.hasShiftDown());
	}

	@Shadow public abstract boolean isEmpty();
	@Shadow public abstract Item getItem();
}
