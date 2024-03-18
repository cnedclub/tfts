package tictim.tfts.contents.anglingentry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.angling.AnglingEnvironment;
import tictim.tfts.angling.NibbleBehavior;
import tictim.tfts.contents.AnglingEntries;

import java.util.List;

public record TestAnglingEntry(
		int weight,
		@NotNull List<ItemStack> loots,
		@Override @NotNull NibbleBehavior nibbleBehavior,
		@Override double baitConsumptionChance,
		boolean givesExp
) implements AnglingEntry<TestAnglingEntry>{
	public static final Codec<TestAnglingEntry> CODEC = RecordCodecBuilder.create(b -> b.group(
			Codec.INT.fieldOf("weight").forGetter(TestAnglingEntry::weight),
			ItemStack.CODEC.listOf().fieldOf("loots").forGetter(TestAnglingEntry::loots),
			NibbleBehavior.CODEC.fieldOf("nibble_behavior").forGetter(TestAnglingEntry::nibbleBehavior),
			Codec.doubleRange(0, 1).fieldOf("bait_consumption_chance").forGetter(TestAnglingEntry::baitConsumptionChance),
			Codec.BOOL.optionalFieldOf("gives_exp", true).forGetter(TestAnglingEntry::givesExp)
	).apply(b, TestAnglingEntry::new));

	@Override @NotNull public AnglingEntryType<TestAnglingEntry> type(){
		return AnglingEntries.TEST.get();
	}

	@Override public double getWeight(@NotNull Player player, @NotNull BlockPos pos, @NotNull AnglingEnvironment environment){
		if(!environment.matches(Fluids.WATER)) return 0;
		return this.weight;
	}

	@Override public void getLoot(@NotNull List<ItemStack> loots){
		for(ItemStack loot : this.loots) loots.add(loot.copy());
	}
	@Override public int getExperience(@NotNull RandomSource randomSource){
		return this.givesExp ? randomSource.nextInt(6)+1 : 0;
	}
}
