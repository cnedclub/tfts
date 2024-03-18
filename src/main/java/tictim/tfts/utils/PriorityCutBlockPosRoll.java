package tictim.tfts.utils;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

import static java.lang.Double.POSITIVE_INFINITY;

public final class PriorityCutBlockPosRoll implements WgtRoll<@NotNull BlockPos>{
	private static final Comparator<RollEntry> comp = (a, b) -> Double.compare(b.weight(), a.weight());

	private final PriorityQueue<RollEntry> entries = new ObjectHeapPriorityQueue<>(comp);
	private final double threshold;

	private boolean infinite;
	private double weightSum;

	public PriorityCutBlockPosRoll(double threshold){
		this.threshold = threshold;
	}

	@Override public void add(@NotNull BlockPos entry, double weight){
		if(weight==POSITIVE_INFINITY){
			if(!this.infinite){
				this.infinite = true;
				this.entries.clear();
			}
		}else if(this.infinite) return;
		this.weightSum += weight;
		this.entries.enqueue(new RollEntry(entry.asLong(), weight));
	}

	@Override @Nullable public BlockPos get(@NotNull RandomSource randomSource){
		if(this.entries.isEmpty()) return null;
		if(this.infinite){
			int index = randomSource.nextInt(this.entries.size());
			while(index-->0) this.entries.dequeue(); // NEW SECRET JAVA OPERATOR!! :OOOO it means "index goes down to 0"
			return BlockPos.of(this.entries.dequeue().blockPos());
		}

		double weight = this.weightSum*randomSource.nextDouble()*this.threshold;
		double weightSum = 0;

		while(this.entries.size()>1){
			RollEntry e = this.entries.dequeue();
			weightSum += e.weight;
			if(weightSum>=weight) return BlockPos.of(e.blockPos());
		}
		return BlockPos.of(this.entries.dequeue().blockPos());
	}

	public record RollEntry(long blockPos, double weight){}
}
