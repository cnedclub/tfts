package tictim.tfts.utils;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static java.lang.Double.POSITIVE_INFINITY;

public final class SimpleWgtRoll<T> implements WgtRoll<T>{
	private final ArrayList<RollEntry<T>> entries = new ArrayList<>();

	private boolean infinite;
	private double weightSum;

	@Override public void add(T entry, double weight){
		if(!(weight>0)) return;
		if(weight==POSITIVE_INFINITY){
			if(!this.infinite){
				this.infinite = true;
				this.entries.clear();
			}
		}else if(this.infinite) return;
		this.entries.add(new RollEntry<>(entry, this.weightSum += weight));
	}

	@Override @Nullable public T get(@NotNull RandomSource randomSource){
		if(this.entries.isEmpty()) return null;
		if(this.infinite) return this.entries.get(randomSource.nextInt(this.entries.size())).entry();

		double weight = this.weightSum*randomSource.nextDouble();
		for(int i = 0; i<this.entries.size()-1; i++){
			RollEntry<T> e = this.entries.get(i);
			if(e.weight()>=weight) return e.entry();
		}
		return this.entries.get(this.entries.size()-1).entry();
	}

	public record RollEntry<T>(T entry, double weight){}
}
