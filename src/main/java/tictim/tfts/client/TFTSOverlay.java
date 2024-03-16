package tictim.tfts.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tictim.tfts.contents.entity.TFTSHook;

import java.util.ArrayList;
import java.util.List;

public final class TFTSOverlay implements IGuiOverlay{
	private final List<String> stringList = new ArrayList<>();

	@Override public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
		LocalPlayer player = gui.getMinecraft().player;
		if(player!=null&&player.fishing instanceof TFTSHook hook){
			 // TODO
		}
	}
}
