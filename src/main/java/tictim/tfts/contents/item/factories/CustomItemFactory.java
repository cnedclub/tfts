package tictim.tfts.contents.item.factories;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class CustomItemFactory implements Supplier<@NotNull Item>{
	@Nullable protected final Supplier<Item.Properties> propertyFactory;
	@Nullable protected final Function<Item.Properties, ? extends Item> itemFactory;

	public CustomItemFactory(@Nullable Supplier<Item.Properties> property){
		this(property, null);
	}
	public CustomItemFactory(@Nullable Function<Item.Properties, ? extends Item> item){
		this(null, item);
	}
	public CustomItemFactory(@Nullable Supplier<Item.Properties> property,
	                         @Nullable Function<Item.Properties, ? extends Item> item){
		this.propertyFactory = property;
		this.itemFactory = item;
	}

	@Override @NotNull public Item get(){
		return this.itemFactory==null ? new Item(getProperties()) : this.itemFactory.apply(getProperties());
	}

	protected Item.Properties getProperties(){
		return this.propertyFactory==null ? new Item.Properties() : this.propertyFactory.get();
	}
}
