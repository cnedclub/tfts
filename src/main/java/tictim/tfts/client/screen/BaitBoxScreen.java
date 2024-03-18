package tictim.tfts.client.screen;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.client.Textures;
import tictim.tfts.contents.inventory.BaitBoxMenu;

public class BaitBoxScreen extends AbstractContainerScreen<BaitBoxMenu>{
	public BaitBoxScreen(BaitBoxMenu menu, Inventory inv, Component title){
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

	@Override protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mx, int my){
		graphics.blit(Textures.BAIT_BOX,
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

	@Override protected void renderLabels(GuiGraphics graphics, int mx, int my){
		graphics.drawCenteredString(this.font, this.title, this.titleLabelX, this.titleLabelY, -1);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -1);

		@Nullable Slot baitBoxSlot = this.menu.baitBoxSlot();
		if(baitBoxSlot!=null){
			RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			// idk if this works
			graphics.blit(Textures.LOCKED_SLOT,
					baitBoxSlot.x, baitBoxSlot.y,
					0, 0,
					16, 16,
					16, 16);
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
		}
	}
}
