package datagen;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.bait.BaitStat;
import tictim.tfts.contents.bait.BaitType;

public final class BaitStatBuilder{
	private final Object2DoubleMap<BaitType> map = new Object2DoubleOpenHashMap<>();

	public BaitStatBuilder stat(@NotNull String type, double value){
		return stat(BaitType.of(type), value);
	}

	public BaitStatBuilder stat(@NotNull BaitType type, double value){
		this.map.put(type, value);
		return this;
	}

	public BaitStat create(){
		return new BaitStat(this.map);
	}
}
