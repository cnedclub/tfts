package tictim.tfts.contents.item.factories;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BlockItemFactory extends CustomItemFactory{
	protected final Supplier<@NotNull Block> block;
	@Nullable protected final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;

	public BlockItemFactory(@NotNull Supplier<@NotNull Block> block){
		this(block, null, null);
	}
	public BlockItemFactory(@NotNull Supplier<@NotNull Block> block,
	                        @Nullable Supplier<Item.Properties> property){
		this(block, property, null);
	}
	public BlockItemFactory(@NotNull Supplier<@NotNull Block> block,
	                        @Nullable BiFunction<Block, Item.Properties, ? extends BlockItem> item){
		this(block, null, item);
	}
	public BlockItemFactory(@NotNull Supplier<@NotNull Block> block,
	                        @Nullable Supplier<Item.Properties> property,
	                        @Nullable BiFunction<Block, Item.Properties, ? extends BlockItem> item){
		super(property, null);
		this.block = Objects.requireNonNull(block, "block == null");
		this.blockItemFactory = item;
	}

	@Override @NotNull public Item get(){
		Block block = this.block.get();
		// the non-nullability of returned value is not set in stone intellij wtf do you mean i need a noinspection for this
		// noinspection ConstantValue
		if(block==null) throw new IllegalStateException("Cannot resolve block reference");
		return this.blockItemFactory==null ?
				new BlockItem(block, getProperties()) :
				this.blockItemFactory.apply(block, getProperties());
	}
}
