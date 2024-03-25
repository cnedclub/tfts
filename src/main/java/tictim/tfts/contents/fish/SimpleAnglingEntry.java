package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.condition.FishCondition;
import tictim.tfts.utils.A;

import java.util.List;

public record SimpleAnglingEntry(
		double baseWeight,
		double weightGrowth,
		double minFishingPower,
		@NotNull List<FishCondition<?>> conditions,
		@NotNull FishEnv environment,
		@NotNull BaitModifierFunction baitModifierFunction,
		@NotNull List<@NotNull ItemStack> loots,
		@NotNull NibbleBehavior nibbleBehavior,
		double baitConsumptionChance,
		boolean givesExp
) implements AnglingEntry<SimpleAnglingEntry>{
	private static final Codec<SimpleAnglingEntry> CODEC = RecordCodecBuilder.create(b -> b.group(
			A.DOUBLE_INFINITE.fieldOf("base_weight").forGetter(SimpleAnglingEntry::baseWeight),
			A.DOUBLE_INFINITE.fieldOf("weight_growth").forGetter(SimpleAnglingEntry::weightGrowth),
			A.DOUBLE_INFINITE.fieldOf("min_fishing_power").forGetter(SimpleAnglingEntry::minFishingPower),
			TFTSRegistries.FISH_CONDITION_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(SimpleAnglingEntry::conditions),
			FishEnv.CODEC.fieldOf("environment").forGetter(SimpleAnglingEntry::environment),
			BaitModifierFunction.CODEC.fieldOf("bait_modifier").forGetter(SimpleAnglingEntry::baitModifierFunction),
			ItemStack.CODEC.listOf().fieldOf("loots").forGetter(SimpleAnglingEntry::loots),
			NibbleBehavior.CODEC.fieldOf("nibble_behavior").forGetter(SimpleAnglingEntry::nibbleBehavior),
			Codec.doubleRange(0, 1).fieldOf("bait_consumption_chance").forGetter(SimpleAnglingEntry::baitConsumptionChance),
			Codec.BOOL.optionalFieldOf("gives_exp", true).forGetter(SimpleAnglingEntry::givesExp)
	).apply(b, SimpleAnglingEntry::new));

	public static final AnglingEntryType<SimpleAnglingEntry> TYPE = new AnglingEntryType<>(TFTSMod.id("angling"), CODEC);

	@Override @NotNull public AnglingEntryType<SimpleAnglingEntry> type(){
		return TYPE;
	}

	@Override public double getWeight(@NotNull AnglingContext context){
		for(FishCondition<?> condition : this.conditions){
			if(!condition.matches(context)) return 0;
		}
		double baitModifier = this.baitModifierFunction.getModifier(context.bait());
		if(!(baitModifier>0)) return 0;

		double fp = context.environment.getBaseFishingPower(this.environment)+context.additionalFishingPower();
		if(fp>=this.minFishingPower){
			return baitModifier*(this.baseWeight+fp*this.weightGrowth);
		}
		return 0;
	}

	@Override @NotNull public NibbleBehavior getNibbleBehavior(@NotNull AnglingContext context){
		return this.nibbleBehavior;
	}

	@Override public double getBaitConsumptionChance(@NotNull AnglingContext context){
		return this.baitConsumptionChance;
	}

	@Override public void getLoot(@NotNull AnglingContext context, @Nullable ItemStack retrievingItem,
	                              @NotNull RandomSource random, @NotNull List<ItemStack> loots){
		for(ItemStack loot : this.loots) loots.add(loot.copy());
	}
	@Override public int getExperience(@NotNull AnglingContext context, @NotNull RandomSource random){
		return this.givesExp ? random.nextInt(6)+1 : 0;
	}
}
