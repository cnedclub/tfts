package tictim.tfts.contents;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.fish.AnglingEntry;
import tictim.tfts.contents.fish.AnglingEntryType;
import tictim.tfts.contents.fish.BaitStat;
import tictim.tfts.contents.fish.condition.FishCondition;
import tictim.tfts.contents.fish.condition.FishConditionType;
import tictim.tfts.contents.item.Bait;
import tictim.tfts.contents.item.Fish;
import tictim.tfts.contents.item.TFTSFishingRodItem;
import tictim.tfts.contents.item.Thing;
import tictim.tfts.contents.recipe.FilletRecipe;
import tictim.tfts.contents.recipe.GrindingRecipe;
import tictim.tfts.contents.recipe.SimpleFilletRecipe;
import tictim.tfts.contents.recipe.SimpleGrindingRecipe;

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

	public static final DeferredRegister<AnglingEntryType<?>> ANGLING_ENTRY_TYPES = DeferredRegister.create(id("angling_entry_types"), MODID);
	public static final DeferredRegister<FishConditionType<?>> FISH_CONDITION_TYPES = DeferredRegister.create(id("fish_condition_types"), MODID);

	public static final ResourceKey<Registry<AnglingEntry<?>>> ANGLING_ENTRY_REGISTRY_KEY = ResourceKey.createRegistryKey(id("angling_entries"));
	public static final ResourceKey<Registry<FishCondition<?>>> FISH_CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(id("fish_conditions"));
	public static final ResourceKey<Registry<BaitStat>> BAIT_STAT_REGISTRY_KEY = ResourceKey.createRegistryKey(id("bait_stats"));

	private static Supplier<IForgeRegistry<AnglingEntryType<?>>> anglingEntryTypeRegistry;
	private static Supplier<IForgeRegistry<FishConditionType<?>>> fishConditionTypeRegistry;

	public static final Codec<AnglingEntry<?>> ANGLING_ENTRY_CODEC = ExtraCodecs.lazyInitializedCodec(() ->
			anglingEntryTypeRegistry().getCodec().dispatch(AnglingEntry::type, AnglingEntryType::codec));

	public static final Codec<FishCondition<?>> FISH_CONDITION_CODEC = ExtraCodecs.lazyInitializedCodec(() ->
			fishConditionTypeRegistry().getCodec().dispatch(FishCondition::type, FishConditionType::codec));

	@ApiStatus.Internal
	public static void init(IEventBus bus){
		BLOCKS.register(bus);
		ITEMS.register(bus);
		BLOCK_ENTITIES.register(bus);
		ENTITIES.register(bus);
		MENUS.register(bus);
		ANGLING_ENTRY_TYPES.register(bus);

		bus.addListener((NewRegistryEvent event) -> {
			anglingEntryTypeRegistry = event.create(
					new RegistryBuilder<AnglingEntryType<?>>()
							.setName(ANGLING_ENTRY_TYPES.getRegistryName())
							.disableSaving()
							.disableSync());
			fishConditionTypeRegistry = event.create(
					new RegistryBuilder<FishConditionType<?>>()
							.setName(FISH_CONDITION_TYPES.getRegistryName())
							.disableSaving()
							.disableSync());
		});
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(ANGLING_ENTRY_REGISTRY_KEY, ANGLING_ENTRY_CODEC);
			event.dataPackRegistry(FISH_CONDITION_REGISTRY_KEY, FISH_CONDITION_CODEC);
			event.dataPackRegistry(BAIT_STAT_REGISTRY_KEY, BaitStat.CODEC, BaitStat.CODEC);
		});

		TFTSBlocks.init();
		TFTSEntities.init();
		AnglingEntries.init();

		Fish.init();
		Bait.init();
		Thing.init();

		bus.addListener(TFTSRegistries::register);
	}

	@NotNull public static IForgeRegistry<AnglingEntryType<?>> anglingEntryTypeRegistry(){
		var reg = anglingEntryTypeRegistry.get();
		if(reg==null) throw new IllegalStateException("Type registry is unavailable");
		return reg;
	}

	@NotNull public static IForgeRegistry<FishConditionType<?>> fishConditionTypeRegistry(){
		var reg = fishConditionTypeRegistry.get();
		if(reg==null) throw new IllegalStateException("Type registry is unavailable");
		return reg;
	}

	private static void register(RegisterEvent event){
		event.register(Registries.ITEM, new ResourceLocation("fishing_rod"),
				() -> new TFTSFishingRodItem(new Item.Properties()));

		event.register(Registries.CREATIVE_MODE_TAB, TFTSRegistries::registerTabs);
		event.register(Registries.RECIPE_TYPE, TFTSRegistries::registerRecipeTypes);
		event.register(Registries.RECIPE_SERIALIZER, TFTSRegistries::registerRecipeSerializers);
	}

	private static void registerTabs(RegisterEvent.RegisterHelper<CreativeModeTab> h){
		ResourceLocation mainTab = id("main");
		ResourceLocation fishTab = id("fish");

		h.register(mainTab, CreativeModeTab.builder()
				.title(Component.translatable("item_group."+MODID))
				.icon(() -> new ItemStack(Items.FISHING_ROD))
				.displayItems((p, o) -> {
					o.accept(Items.FISHING_ROD);

					for(Thing thing : Thing.values()) o.accept(thing);
					for(Bait bait : Bait.values()) o.accept(bait);
				})
				.build());
		h.register(fishTab, CreativeModeTab.builder()
				.title(Component.translatable("item_group."+MODID+".fish"))
				.icon(() -> new ItemStack(Fish.BASS)).displayItems((p, o) -> {
					for(Fish fish : Fish.values()) o.accept(fish);
				})
				.withTabsBefore(mainTab)
				.build());
	}

	private static void registerRecipeTypes(RegisterEvent.RegisterHelper<RecipeType<?>> h){
		h.register(FilletRecipe.TYPE.id(), FilletRecipe.TYPE);
		h.register(GrindingRecipe.TYPE.id(), GrindingRecipe.TYPE);
	}

	private static void registerRecipeSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> h){
		h.register(id("fillet"), SimpleFilletRecipe.SERIALIZER);
		h.register(id("grinding"), SimpleGrindingRecipe.SERIALIZER);
	}
}
