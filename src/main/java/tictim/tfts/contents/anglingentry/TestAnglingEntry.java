package tictim.tfts.contents.anglingentry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.angling.AnglingEnvironment;
import tictim.tfts.contents.AnglingEntries;

import java.util.List;

public record TestAnglingEntry(int weight) implements AnglingEntry<TestAnglingEntry>{
	public static final Codec<TestAnglingEntry> CODEC = RecordCodecBuilder.create(b -> b.group(
			Codec.INT.fieldOf("weight").forGetter(TestAnglingEntry::weight)
	).apply(b, TestAnglingEntry::new));

	@Override @NotNull public AnglingEntryType<TestAnglingEntry> type(){
		return AnglingEntries.TEST.get();
	}

	@Override public double getWeight(@NotNull Player player, @NotNull BlockPos pos, @NotNull AnglingEnvironment environment){
		if(!environment.matches(Fluids.WATER)) return 0;
		return this.weight;
	}

	@Override public void getLoot(@NotNull List<ItemStack> loots){
		loots.add(new ItemStack(Items.COBBLESTONE));
	}
}
