package tictim.tfts.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public abstract class TFTSScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>{
	public TFTSScreen(T menu, Inventory inv, Component title){
		super(menu, inv, title);
		this.titleLabelX = this.imageWidth/2;
		this.titleLabelY = -10;
		this.inventoryLabelY = this.imageHeight-90-10;
	}

	@Override public void render(@NotNull GuiGraphics graphics, int mx, int my, float partialTick){
		this.renderBackground(graphics);
		super.render(graphics, mx, my, partialTick);
		this.renderTooltip(graphics, mx, my);
	}

	@Override protected void renderLabels(GuiGraphics graphics, int mx, int my){
		graphics.drawCenteredString(this.font, this.title, this.titleLabelX, this.titleLabelY, -1);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -1);
	}
}
