package tictim.tfts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tictim.tfts.angling.AnglingUtils;
import tictim.tfts.caps.BaitBoxInventory;
import tictim.tfts.contents.entity.TFTSHook;

public final class TFTSOverlay implements IGuiOverlay{
	@Override public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight){
		Minecraft mc = gui.getMinecraft();
		if(mc.screen!=null) return;
		LocalPlayer player = mc.player;
		if(player==null) return;
		if(AnglingUtils.getFishingHand(player)==null) return;

		BaitBoxInventory inv = AnglingUtils.getBaitBoxInventory(player);
		if(inv!=null){
			int slots = inv.getInventory().getSlots();
			int xStart = screenWidth*4/6;
			int height = 16+slots*18;
			int yStart = (screenHeight-height)/2;

			graphics.blit(Textures.BAIT_OVERLAY,
					xStart, yStart,
					0, 0,
					34, 8,
					34, 34);
			graphics.blit(Textures.BAIT_OVERLAY,
					xStart, yStart+height-8,
					0, 26,
					34, 8,
					34, 34);

			for(int i = 0; i<slots; i++){
				graphics.blit(inv.selectedIndex()==i ? Textures.BAIT_OVERLAY_SELECTED : Textures.BAIT_OVERLAY,
						xStart, yStart+8+18*i,
						0, 8,
						34, 18,
						34, 34);
			}
			for(int i = 0; i<slots; i++){
				ItemStack stack = inv.getInventory().getStackInSlot(i);
				if(stack.isEmpty()) continue;
				graphics.renderItem(stack, xStart+9, yStart+9+18*i);
				graphics.renderItemDecorations(gui.getFont(), stack, xStart+9, yStart+9+18*i);
			}
		}

		if(player.fishing instanceof TFTSHook hook){
			// TODO
		}
	}
}
