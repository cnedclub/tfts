package tictim.tfts.contents.fish;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public final class FluidAnglingEnvironment implements AnglingEnvironment{
	private final Fluid fluid;

	private final Object2IntMap<Biome> fluidBlockCount = new Object2IntOpenHashMap<>();
	private final Object2DoubleMap<PrimitiveFishEnv> fishingPowerCache = new Object2DoubleOpenHashMap<>();

	public FluidAnglingEnvironment(@NotNull Fluid fluid){
		this.fluid = fluid;
	}

	public void eval(@NotNull ServerLevel level, @NotNull BlockPos origin){
		this.fluidBlockCount.clear();

		AnglingUtils.traverseFluid(level, origin, this.fluid, pos -> {
			Biome biome = level.getBiome(pos).get();
			this.fluidBlockCount.put(biome, this.fluidBlockCount.getInt(biome)+1);
		});
	}

	@Override public double getBaseFishingPower(@NotNull FishEnv fishEnv){
		double sum = 0;

		for(PrimitiveFishEnv env : fishEnv.asSet()){
			if(isApplicable(env)){
				sum += this.fishingPowerCache.computeIfAbsent(env, this::calculateFishingPower);
			}
		}
		return sum;
	}

	private boolean isApplicable(@NotNull PrimitiveFishEnv fishEnv){
		return this.fluid.isSame(Fluids.WATER); // TODO only water fishing... for now
	}

	private double calculateFishingPower(@NotNull PrimitiveFishEnv fishEnv){
		long poolSize = 0;
		for(var biome : fishEnv.biomes()) poolSize += this.fluidBlockCount.getInt(biome);
		return fishEnv.calculateFishingPower(poolSize);
	}

	@Override public void processLoot(@NotNull ItemEntity itemEntity){
		// TODO how the fuck do i make items and exps fire immune????????????? mixins???????????????????????????????????
	}
	@Override public void processExp(@NotNull ExperienceOrb experienceOrb){
		// TODO Today was Sonic's birthday, and all his friends came to the party down in Green Hills Zone. Tails was there, Knuckles, even Shadow. Sonic was so happy, but he wished one more friend was there. His super secret special friend. Eggman. Sure he and Eggman fought, but it was for show. The truth was Sonic and Eggman were deeply in love. Everytime Eggman would be up to no good, Sonic would chase after him and they would continue their mischief in there bedroom.
	}

	@Override public String toString(){
		return "FluidAnglingEnvironment { "+fluidBlockCount.object2IntEntrySet().stream()
				.map(e -> e.getKey()+": "+e.getIntValue())
				.collect(Collectors.joining(", "))+" }";
	}
}
