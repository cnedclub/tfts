package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.AnglingEntries;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.condition.FishCondition;

import java.util.List;

public record SimpleAnglingEntry(
		double weight,
		@NotNull List<FishCondition<?>> conditions,
		@NotNull FishEnv environment,
		double envBonus,
		@NotNull BaitModifierFunction baitModifierFunction,
		@NotNull List<@NotNull ItemStack> loots,
		@Override @NotNull NibbleBehavior nibbleBehavior,
		@Override double baitConsumptionChance,
		boolean givesExp
) implements AnglingEntry<SimpleAnglingEntry>{
	private static final Codec<SimpleAnglingEntry> CODEC = RecordCodecBuilder.create(b -> b.group(
			Codec.DOUBLE.fieldOf("weight").forGetter(SimpleAnglingEntry::weight),
			TFTSRegistries.FISH_CONDITION_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(SimpleAnglingEntry::conditions),
			FishEnv.CODEC.fieldOf("environment").forGetter(SimpleAnglingEntry::environment),
			Codec.DOUBLE.fieldOf("environment_bonus").forGetter(SimpleAnglingEntry::envBonus),
			BaitModifierFunction.CODEC.fieldOf("baits").forGetter(SimpleAnglingEntry::baitModifierFunction),
			ItemStack.CODEC.listOf().fieldOf("loots").forGetter(SimpleAnglingEntry::loots),
			NibbleBehavior.CODEC.fieldOf("nibble_behavior").forGetter(SimpleAnglingEntry::nibbleBehavior),
			Codec.doubleRange(0, 1).fieldOf("bait_consumption_chance").forGetter(SimpleAnglingEntry::baitConsumptionChance),
			Codec.BOOL.optionalFieldOf("gives_exp", true).forGetter(SimpleAnglingEntry::givesExp)
	).apply(b, SimpleAnglingEntry::new));

	public static final AnglingEntryType<SimpleAnglingEntry> TYPE = new AnglingEntryType<>(CODEC);

	@Override @NotNull public AnglingEntryType<SimpleAnglingEntry> type(){
		return TYPE;
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
