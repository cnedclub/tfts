package tictim.tfts.contents.bait;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.stream.Collectors;

public final class BaitStat{
	public static final Codec<BaitStat> CODEC = Codec.unboundedMap(BaitType.CODEC, Codec.DOUBLE)
			.xmap(BaitStat::new, BaitStat::rawStats);

	private final Object2DoubleMap<BaitType> rawStats = new Object2DoubleAVLTreeMap<>(BaitTypeComparator.get());
	private final Object2DoubleMap<BaitType> propagatedStats = new Object2DoubleOpenHashMap<>();
	private final Object2DoubleMap<BaitType> propagatedStatsSorted = new Object2DoubleAVLTreeMap<>(BaitTypeComparator.get());

	public BaitStat(Map<BaitType, Double> stats){
		this.rawStats.putAll(stats);

		Object2IntMap<BaitType> depthCache = new Object2IntOpenHashMap<>();
		depthCache.defaultReturnValue(Integer.MAX_VALUE);

		for(var e : this.rawStats.object2DoubleEntrySet()){
			double value = e.getDoubleValue();
			if(!(value>0)) return;
			BaitType type = e.getKey().parent();
			int depth = 0;
			for(; type!=null; type = type.parent(), depth++){
				int d = depthCache.getInt(type);

				// the entry with depth closer to propagated type wins
				if(d<depth) continue;
				// if depth of two entries are equal, the highest value is used
				if(d==depth&&this.propagatedStats.getDouble(type)>=value) continue;

				depthCache.put(type, depth);
				this.propagatedStats.put(type, value);
			}
		}

		this.propagatedStats.putAll(this.rawStats);

		this.propagatedStatsSorted.putAll(this.propagatedStats);
	}

	public double get(@NotNull BaitType type){
		return this.propagatedStats.getDouble(type);
	}

	@NotNull @Unmodifiable public Object2DoubleMap<BaitType> rawStats(){
		return Object2DoubleMaps.unmodifiable(this.rawStats);
	}
	@NotNull @Unmodifiable public Object2DoubleMap<BaitType> allStats(){
		return Object2DoubleMaps.unmodifiable(this.propagatedStatsSorted);
	}

	@Override public int hashCode(){
		return this.propagatedStats.hashCode();
	}

	@Override public boolean equals(Object obj){
		if(obj==this) return true;
		if(obj==null) return false;
		return obj instanceof BaitStat s&&this.propagatedStats.equals(s.propagatedStats);
	}

	@Override public String toString(){
		return "BaitStat { "+propagatedStatsSorted.object2DoubleEntrySet().stream()
				.map(e -> e.getKey()+": "+e.getDoubleValue())
				.collect(Collectors.joining(", "))+" }";
	}
}
