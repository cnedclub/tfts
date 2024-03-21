package tictim.tfts.client;

import net.minecraft.resources.ResourceLocation;

import static tictim.tfts.TFTSMod.id;

public final class Textures{
	private Textures(){}

	// 176x66
	public static final ResourceLocation BAIT_BOX = id("textures/gui/bait_box.png");
	public static final ResourceLocation FISH_PREPARATION_TABLE = id("textures/gui/fish_preparation_table.png");
	// 176x90
	public static final ResourceLocation INVENTORY = id("textures/gui/inventory.png");

	// 16x16
	public static final ResourceLocation LOCKED_SLOT = id("textures/gui/locked_slot.png");

	// 34x34
	public static final ResourceLocation BAIT_OVERLAY = id("textures/gui/bait_overlay.png");
	public static final ResourceLocation BAIT_OVERLAY_SELECTED = id("textures/gui/bait_overlay_selected.png");
}
