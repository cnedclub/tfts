package tictim.tfts.client;

import it.unimi.dsi.fastutil.doubles.Double2IntMap;
import it.unimi.dsi.fastutil.doubles.Double2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.fish.BaitStat;

import java.util.List;

public final class ClientUtils{
	private ClientUtils(){}

	private static final Double2IntMap baitStatColorCache = new Double2IntOpenHashMap();

	private static final int[] baitStatColor = {0x511212, 0xb33f0a, 0xdf9013, 0xeca816, 0x8aca0e, 0x30d80e};
	private static final double[] baitStatValue = {0, 0.5, 1, 1.25, 1.75, 2};

	public static void addBaitStatText(@NotNull List<Component> text, @NotNull BaitStat stat, boolean debug){
		for(var e : (debug ? stat.allStats() : stat.allStatsSorted()).object2DoubleEntrySet()){
			String key = e.getKey().getTranslationKey();
			if(I18n.exists(key)){
				if(debug){
					text.add(Component.translatable("item.tfts.generic.tooltip.bait_stat.debug",
							Component.translatable(key).withStyle(ChatFormatting.YELLOW),
							e.getKey().toString(),
							getBaitStatValueText(e.getDoubleValue())));
				}else{
					text.add(Component.translatable("item.tfts.generic.tooltip.bait_stat",
							Component.translatable(key).withStyle(ChatFormatting.YELLOW),
							getBaitStatValueText(e.getDoubleValue())));
				}
			}else if(debug){
				text.add(Component.translatable("item.tfts.generic.tooltip.bait_stat.debug_no_translation",
						e.getKey().toString(),
						getBaitStatValueText(e.getDoubleValue())));
			}
		}
	}

	private static Component getBaitStatValueText(double stat){
		return Component.literal(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(stat))
				.withStyle(Style.EMPTY.withColor(getBaitStatColor(stat)));
	}

	private static int getBaitStatColor(double stat){
		return baitStatColorCache.computeIfAbsent(stat, d -> {
			if(!(d>0)) return baitStatColor[0];
			for(int i = 1; i<baitStatColor.length; i++){
				if(d<baitStatValue[i]){
					return FastColor.ARGB32.lerp(
							(float)((d-baitStatValue[i-1])/(baitStatValue[i]-baitStatValue[i-1])),
							baitStatColor[i-1], baitStatColor[i]);
				}
			}
			return baitStatColor[baitStatColor.length-1];
		});
	}
}
