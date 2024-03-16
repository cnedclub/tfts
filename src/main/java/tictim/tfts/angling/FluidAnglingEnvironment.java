package tictim.tfts.angling;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;

import java.util.Set;
import java.util.stream.Collectors;

public final class FluidAnglingEnvironment implements AnglingEnvironment{
	private final Fluid fluid;

	private final Object2IntMap<Biome> baseFishingPower = new Object2IntOpenHashMap<>();

	public FluidAnglingEnvironment(@NotNull Fluid fluid){
		this.fluid = fluid;
	}

	public void eval(@NotNull Player player,
	                 @NotNull BlockPos origin){
		this.baseFishingPower.clear();
		Level level = player.level();
		long ns = System.nanoTime();
		AnglingUtils.traverseFluid(level, origin, this.fluid, pos -> {
			Biome biome = level.getBiome(pos).get();
			this.baseFishingPower.put(biome, this.baseFishingPower.getInt(biome)+1);
		});
		ns = System.nanoTime()-ns;
		TFTSMod.LOGGER.info("Env scan finished at {} ms, {}", ((double)ns/1_000_000), this);
	}

	@Override public boolean matches(@NotNull Fluid fluid){
		return this.fluid.isSame(fluid);
	}

	@Override public double getBaseFishingPower(@Nullable Set<Biome> biomes){
		long sum = 0;
		if(biomes!=null){
			for(Biome biome : biomes) sum += baseFishingPower.getInt(biome);
		}else{
			IntIterator it = baseFishingPower.values().intIterator();
			while(it.hasNext()) sum += it.nextInt();
		}
		return sum;
	}

	@Override public String toString(){
		return "FluidAnglingEnvironment { "+baseFishingPower.object2IntEntrySet().stream()
				.map(e -> e.getKey()+": "+e.getIntValue())
				.collect(Collectors.joining(", "))+" }";
	}
}
