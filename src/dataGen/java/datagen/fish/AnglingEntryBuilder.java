package datagen.fish;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.fish.*;
import tictim.tfts.contents.fish.condition.FishCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class AnglingEntryBuilder<T extends AnglingEntry<T>>{
	public static AnglingEntryBuilder<SimpleAnglingEntry> simpleFish(){
		return new AnglingEntryBuilder<>(b -> new SimpleAnglingEntry(
				b.baseWeight(),
				b.weightGrowth(),
				b.minFishingPower(),
				b.conditions(),
				b.environment(),
				b.baitModifier(),
				b.loots(),
				b.nibbleBehavior(),
				b.baitConsumptionChance(),
				b.givesExp()
		));
	}
	public static AnglingEntryBuilder<TrashAnglingEntry> trash(){
		return new AnglingEntryBuilder<>(b -> new TrashAnglingEntry(
				b.baseWeight(),
				b.weightGrowth(),
				b.minWeight(),
				b.maxWeight(),
				b.conditions(),
				b.environment()
		));
	}

	private final Function<AnglingEntryBuilder<T>, T> factory;

	private double baseWeight;
	private double weightGrowth = 1;
	private double minFishingPower = Double.NaN;
	private double minWeight = 0;
	private double maxWeight = Double.POSITIVE_INFINITY;
	private final List<FishCondition<?>> conditions = new ArrayList<>();
	private FishEnv environment;
	private final List<ItemStack> loots = new ArrayList<>();
	private BaitModifierFunction baitModifier;
	private NibbleBehavior nibbleBehavior;
	private double baitConsumptionChance = .1;
	private boolean givesExp;

	public AnglingEntryBuilder(@NotNull Function<AnglingEntryBuilder<T>, T> factory){
		this.factory = Objects.requireNonNull(factory, "factory == null");
	}

	public double baseWeight(){
		if(baseWeight<=0) throw new IllegalStateException("Property 'baseWeight' not set");
		return baseWeight;
	}
	public List<FishCondition<?>> conditions(){
		return conditions;
	}
	public FishEnv environment(){
		if(environment==null) throw new IllegalStateException("Property 'environment' not set");
		return environment;
	}
	public double minFishingPower(){
		if(Double.isNaN(minFishingPower)) throw new IllegalStateException("Property 'minFishingPower' not set");
		return minFishingPower;
	}
	public double weightGrowth(){
		return weightGrowth;
	}
	public double minWeight(){
		return minWeight;
	}
	public double maxWeight(){
		return maxWeight;
	}
	public BaitModifierFunction baitModifier(){
		if(baitModifier==null) throw new IllegalStateException("Property 'baitModifier' not set");
		return baitModifier;
	}
	public List<ItemStack> loots(){
		if(loots.isEmpty()) throw new IllegalStateException("Property 'loots' not set");
		return loots;
	}
	public NibbleBehavior nibbleBehavior(){
		if(nibbleBehavior==null) throw new IllegalStateException("Property 'nibbleBehavior' not set");
		return nibbleBehavior;
	}
	public double baitConsumptionChance(){
		return baitConsumptionChance;
	}
	public boolean givesExp(){
		return givesExp;
	}

	public AnglingEntryBuilder<T> baseWeight(double baseWeight){
		this.baseWeight = baseWeight;
		return this;
	}
	public AnglingEntryBuilder<T> weightGrowth(double weightGrowth){
		this.weightGrowth = weightGrowth;
		return this;
	}
	public AnglingEntryBuilder<T> minFishingPower(double minFishingPower){
		this.minFishingPower = minFishingPower;
		return this;
	}
	public AnglingEntryBuilder<T> minWeight(double minWeight){
		this.minWeight = minWeight;
		return this;
	}
	public AnglingEntryBuilder<T> maxWeight(double maxWeight){
		this.maxWeight = maxWeight;
		return this;
	}
	public AnglingEntryBuilder<T> condition(FishCondition<?> condition){
		this.conditions.add(condition);
		return this;
	}
	public AnglingEntryBuilder<T> env(FishEnv... environment){
		this.environment = FishEnv.of(environment);
		return this;
	}
	public AnglingEntryBuilder<T> bait(BaitModifierCondition condition, double modifier){
		return bait(FishUtils.c(condition, modifier));
	}
	public AnglingEntryBuilder<T> bait(BaitModifierFunction baitModifier){
		if(this.baitModifier!=null) TFTSMod.LOGGER.warn("Overriding bait");
		this.baitModifier = baitModifier;
		return this;
	}
	public AnglingEntryBuilder<T> loot(ItemLike item){
		return loot(item, 1);
	}
	public AnglingEntryBuilder<T> loot(ItemLike item, int count){
		return loot(new ItemStack(item, count));
	}
	public AnglingEntryBuilder<T> loot(ItemStack loot){
		this.loots.add(loot);
		return this;
	}
	public AnglingEntryBuilder<T> nibbleBehavior(NibbleBehavior nibbleBehavior){
		this.nibbleBehavior = nibbleBehavior;
		return this;
	}
	public AnglingEntryBuilder<T> baitConsumptionChance(double baitConsumptionChance){
		this.baitConsumptionChance = baitConsumptionChance;
		return this;
	}
	public AnglingEntryBuilder<T> givesExp(boolean givesExp){
		this.givesExp = givesExp;
		return this;
	}
	public AnglingEntryBuilder<T> noExp(){
		this.givesExp = false;
		return this;
	}

	public T create(){
		return this.factory.apply(this);
	}
}
