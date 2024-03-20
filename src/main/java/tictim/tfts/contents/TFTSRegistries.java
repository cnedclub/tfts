package tictim.tfts.contents;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.anglingentry.AnglingEntry;
import tictim.tfts.contents.anglingentry.AnglingEntryType;

import java.util.function.Supplier;

import static tictim.tfts.TFTSMod.MODID;
import static tictim.tfts.TFTSMod.id;

public final class TFTSRegistries{
	private TFTSRegistries(){}

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
	public static final DeferredRegister<AnglingEntryType<?>> ANGLING_ENTRY_TYPES = DeferredRegister.create(id("angling_entry_type"), MODID);

	public static final ResourceKey<Registry<AnglingEntry<?>>> ANGLING_ENTRY_REGISTRY_KEY = ResourceKey.createRegistryKey(id("angling_entry"));

	private static Supplier<IForgeRegistry<AnglingEntryType<?>>> typeRegistry;

	@ApiStatus.Internal
	public static void init(IEventBus bus){
		BLOCKS.register(bus);
		ITEMS.register(bus);
		BLOCK_ENTITIES.register(bus);
		ENTITIES.register(bus);
		MENUS.register(bus);
		ANGLING_ENTRY_TYPES.register(bus);

		bus.addListener((NewRegistryEvent event) -> {
			typeRegistry = event.create(
					new RegistryBuilder<AnglingEntryType<?>>()
							.setName(id("angling_entry_type"))
							.disableSaving()
							.disableSync());
		});
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(ANGLING_ENTRY_REGISTRY_KEY,
					ExtraCodecs.lazyInitializedCodec(() -> anglingEntryTypeRegistry().getCodec()
							.dispatch(AnglingEntry::type, AnglingEntryType::codec)));
		});

		TFTSBlocks.init();
		TFTSItems.init();
		TFTSEntities.init();
		AnglingEntries.init();
	}

	@NotNull
	public static IForgeRegistry<AnglingEntryType<?>> anglingEntryTypeRegistry(){
		IForgeRegistry<AnglingEntryType<?>> reg = typeRegistry.get();
		if(reg==null) throw new IllegalStateException("Type registry is unavailable");
		return reg;
	}
}
