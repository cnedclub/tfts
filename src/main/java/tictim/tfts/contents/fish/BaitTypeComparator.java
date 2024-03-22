package tictim.tfts.contents.fish;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public final class BaitTypeComparator implements Comparator<BaitType>{
	private BaitTypeComparator(){}

	private static final BaitTypeComparator instance = new BaitTypeComparator();

	@NotNull public static BaitTypeComparator get(){
		return instance;
	}

	@Override public int compare(BaitType a, BaitType b){
		if(a==b) return 0;
		if(a==null) return -1;
		if(b==null) return 1;
		int c = compare(a.parent(), b.parent());
		if(c!=0) return c;
		return a.text().compareTo(b.text());
	}
}
