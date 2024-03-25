package tictim.tfts.contents.item.factories;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class SimpleItemFactory implements Supplier<@NotNull Item>{
	static final SimpleItemFactory INSTANCE = new SimpleItemFactory();

	private SimpleItemFactory(){}

	@Override @NotNull public Item get(){
		return new Item(new Item.Properties());
	}

	@Override public String toString(){
		return "Simple";
	}
}
