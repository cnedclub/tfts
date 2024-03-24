package tictim.tfts.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tictim.tfts.caps.BaitBoxInventory;
import tictim.tfts.contents.fish.AnglingUtils;
import tictim.tfts.contents.fish.BaitStat;

import java.util.ArrayList;
import java.util.List;

public final class TFTSOverlay implements IGuiOverlay{
	@Override public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight){
		Minecraft mc = gui.getMinecraft();
		if(mc.screen!=null) return;
		LocalPlayer player = mc.player;
		if(player==null) return;
		if(AnglingUtils.getFishingHand(player)==null) return;

		int xStart = screenWidth*4/6, yStart;

		BaitBoxInventory inv = AnglingUtils.getBaitBoxInventory(player);
		if(inv!=null){
			int slots = inv.inventory().getSlots();
			int widgetHeight = 16+slots*18;
			yStart = (screenHeight-widgetHeight)/2;

			graphics.blit(Textures.BAIT_OVERLAY,
					xStart, yStart,
					0, 0,
					34, 8,
					34, 34);
			graphics.blit(Textures.BAIT_OVERLAY,
					xStart, yStart+widgetHeight-8,
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
				ItemStack stack = inv.inventory().getStackInSlot(i);
				if(stack.isEmpty()) continue;
				graphics.renderItem(stack, xStart+9, yStart+9+18*i);
				graphics.renderItemDecorations(gui.getFont(), stack, xStart+9, yStart+9+18*i);
			}
		}else{
			yStart = screenHeight/2;
		}

		if(player.fishing==null){
			ItemStack bait = inv!=null ? inv.inventory().getStackInSlot(inv.selectedIndex()) : ItemStack.EMPTY;
			List<Component> text;

			BaitStat stat = AnglingUtils.getBaitStat(bait, player.connection.registryAccess());
			if(stat!=null){
				text = new ArrayList<>();
				MutableComponent itemName = Component.empty()
						.append(bait.getHoverName())
						.withStyle(bait.getRarity().getStyleModifier());
				if(bait.hasCustomHoverName()) itemName.withStyle(ChatFormatting.ITALIC);
				text.add(itemName);
				AnglingUtils.addBaitStatText(text, stat, false);
			}else{
				text = List.of(Component.translatable("overlay.tfts.no_bait"));
			}
			graphics.renderComponentTooltip(mc.font, text, xStart+40,
					inv!=null ? yStart+8+18*inv.selectedIndex()+4 : screenHeight/2-5);
		}
	}
}
