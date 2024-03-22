package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.condition.FishCondition;
import tictim.tfts.contents.item.Thing;
import tictim.tfts.utils.A;

import java.util.List;

public record TrashAnglingEntry(
		double baseWeight,
		double weightGrowth,
		double minWeight,
		double maxWeight,
		@NotNull List<FishCondition<?>> conditions,
		@NotNull FishEnv environment
) implements AnglingEntry<TrashAnglingEntry>{
	private static final Codec<TrashAnglingEntry> CODEC = RecordCodecBuilder.create(b -> b.group(
			A.DOUBLE_INFINITE.fieldOf("base_weight").forGetter(TrashAnglingEntry::baseWeight),
			A.DOUBLE_INFINITE.fieldOf("weight_growth").forGetter(TrashAnglingEntry::weightGrowth),
			A.DOUBLE_INFINITE.optionalFieldOf("min_weight", 0.0).forGetter(TrashAnglingEntry::minWeight),
			A.DOUBLE_INFINITE.optionalFieldOf("max_weight", Double.POSITIVE_INFINITY).forGetter(TrashAnglingEntry::maxWeight),
			TFTSRegistries.FISH_CONDITION_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(TrashAnglingEntry::conditions),
			FishEnv.CODEC.fieldOf("environment").forGetter(TrashAnglingEntry::environment)
	).apply(b, TrashAnglingEntry::new));

	public static final AnglingEntryType<TrashAnglingEntry> TYPE = new AnglingEntryType<>(CODEC);

	@Override @NotNull public AnglingEntryType<TrashAnglingEntry> type(){
		return TYPE;
	}

	@Override public double getWeight(@NotNull AnglingContext context){
		for(FishCondition<?> condition : this.conditions){
			if(!condition.matches(context)) return 0;
		}

		double fp = context.environment.getBaseFishingPower(this.environment)+context.additionalFishingPower();
		return Mth.clamp(this.baseWeight+fp*this.weightGrowth, this.minWeight, this.maxWeight);
	}

	@Override public void getLoot(@NotNull AnglingContext context, @Nullable ItemStack retrievingItem,
	                              @NotNull RandomSource random, @NotNull List<ItemStack> loots){
		if(random.nextFloat()<.05f){
			loots.add(new ItemStack(Thing.JAJO_COLA));
		}else{
			LootTable lootTable = context.level.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING_JUNK);
			loots.addAll(lootTable.getRandomItems(new LootParams.Builder(context.level)
					.withParameter(LootContextParams.ORIGIN, context.hook.position())
					.withParameter(LootContextParams.TOOL, retrievingItem!=null ? retrievingItem : context.player.getMainHandItem()) // idk
					.withParameter(LootContextParams.THIS_ENTITY, context.hook)
					.withParameter(LootContextParams.KILLER_ENTITY, context.player)
					.withLuck((float)context.hook.luck()+context.player.getLuck())
					.create(LootContextParamSets.FISHING)));
		}
	}

	@Override @NotNull public NibbleBehavior getNibbleBehavior(@NotNull AnglingContext context){
		return NibbleBehavior.none();
	}
	@Override public double getBaitConsumptionChance(@NotNull AnglingContext context){
		return 0;
	}
	@Override public int getExperience(@NotNull AnglingContext context, @NotNull RandomSource random){
		return 0;
	}
}
