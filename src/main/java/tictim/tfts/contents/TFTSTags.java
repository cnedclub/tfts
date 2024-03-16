package tictim.tfts.contents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public final class TFTSTags{
	private TFTSTags(){}

	public static final TagKey<Item> CURIO_BAIT_BOX = ItemTags.create(new ResourceLocation(CuriosApi.MODID, "tfts_bait_box"));
}
