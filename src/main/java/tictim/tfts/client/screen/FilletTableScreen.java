package tictim.tfts.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.client.Textures;
import tictim.tfts.contents.inventory.FilletTableMenu;
import tictim.tfts.net.TFTSNet;
import tictim.tfts.net.messages.FilletMsg;

public class FilletTableScreen extends TFTSScreen<FilletTableMenu>{
	private Button button;

	public FilletTableScreen(FilletTableMenu menu, Inventory inv, Component title){
		super(menu, inv, title);
	}

	@Override protected void init(){
		super.init();
		this.button = addRenderableWidget(Button.builder(Component.empty(),
						btn -> TFTSNet.NET.sendToServer(FilletMsg.get()))
				.bounds(this.leftPos+97+10, this.topPos+10, 18, 18)
				.build());
	}

	@Override public void render(@NotNull GuiGraphics graphics, int mx, int my, float partialTick){
		this.button.active = this.menu.canProcess();
		super.render(graphics, mx, my, partialTick);
	}

	@Override protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mx, int my){
		graphics.blit(Textures.FILLET_TABLE,
				this.leftPos, this.topPos,
				0, 0,
				176, 66,
				176, 66);

		graphics.blit(Textures.INVENTORY,
				this.leftPos, this.topPos+this.imageHeight-90,
				0, 0,
				176, 90,
				176, 90);
	}
}
