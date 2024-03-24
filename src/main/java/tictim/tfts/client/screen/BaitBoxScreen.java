package tictim.tfts.client.screen;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.client.Textures;
import tictim.tfts.contents.inventory.BaitBoxMenu;

public class BaitBoxScreen extends TFTSScreen<BaitBoxMenu>{
	public BaitBoxScreen(BaitBoxMenu menu, Inventory inv, Component title){
		super(menu, inv, title);
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
		super.renderLabels(graphics, mx, my);

		@Nullable Slot baitBoxSlot = this.menu.baitBoxInventorySlot();
		if(baitBoxSlot!=null) renderSlotLock(graphics, baitBoxSlot);

		if(this.menu.isInventoryLocked()){
			for(int i = 0; i<this.menu.baitBoxSize(); i++)
				renderSlotLock(graphics, this.menu.slots.get(i));
		}

		if(this.slotLockRendered){
			this.slotLockRendered = false;

			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
		}
	}

	private boolean slotLockRendered;

	private void renderSlotLock(GuiGraphics graphics, Slot slot){
		if(!this.slotLockRendered){
			this.slotLockRendered = true;
			RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		graphics.blit(Textures.LOCKED_SLOT,
				slot.x, slot.y,
				0, 0,
				16, 16,
				16, 16);
	}
}
