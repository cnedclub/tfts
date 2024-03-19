package tictim.tfts.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.tfts.angling.AnglingUtils;
import tictim.tfts.caps.BaitBoxInventory;
import tictim.tfts.net.TFTSNet;
import tictim.tfts.net.messages.SelectBaitBoxSlotMsg;

import static tictim.tfts.TFTSMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class TFTSClientEventHandlers{
	private TFTSClientEventHandlers(){}

	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
		LocalPlayer player = Minecraft.getInstance().player;
		if(player==null||player.isSpectator()||
				AnglingUtils.getFishingHand(player)==null||
				!Minecraft.getInstance().options.keyShift.isDown()) return;

		event.setCanceled(true);

		if(player.fishing!=null) return;

		BaitBoxInventory baitBox = AnglingUtils.getBaitBoxInventory(player);
		if(baitBox==null) return;
		int size = baitBox.getInventory().getSlots();
		if(size<=0) return;

		int selectedIndex = baitBox.selectedIndex()-(int)Math.signum(event.getScrollDelta());
		while(selectedIndex<0) selectedIndex += size;
		while(selectedIndex>=size) selectedIndex -= size;

		baitBox.setSelectedIndex(selectedIndex);
		TFTSNet.NET.sendToServer(new SelectBaitBoxSlotMsg(selectedIndex));
	}
}
