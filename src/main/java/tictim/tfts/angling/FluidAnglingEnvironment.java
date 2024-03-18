package tictim.tfts.angling;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		AnglingUtils.traverseFluid(level, origin, this.fluid, pos -> {
			Biome biome = level.getBiome(pos).get();
			this.baseFishingPower.put(biome, this.baseFishingPower.getInt(biome)+1);
		});
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

	@Override public void processLoot(@NotNull ItemEntity itemEntity){
		// TODO how the fuck do i make items and exps fire immune????????????? mixins???????????????????????????????????
	}
	@Override public void processExp(@NotNull ExperienceOrb experienceOrb){
		// TODO Today was Sonic's birthday, and all his friends came to the party down in Green Hills Zone. Tails was there, Knuckles, even Shadow. Sonic was so happy, but he wished one more friend was there. His super secret special friend. Eggman. Sure he and Eggman fought, but it was for show. The truth was Sonic and Eggman were deeply in love. Everytime Eggman would be up to no good, Sonic would chase after him and they would continue their mischief in there bedroom.
	}

	@Override public String toString(){
		return "FluidAnglingEnvironment { "+baseFishingPower.object2IntEntrySet().stream()
				.map(e -> e.getKey()+": "+e.getIntValue())
				.collect(Collectors.joining(", "))+" }";
	}
}
