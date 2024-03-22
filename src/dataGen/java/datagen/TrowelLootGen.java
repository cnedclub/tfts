package datagen;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import tictim.tfts.contents.item.Bait;
import tictim.tfts.contents.item.TrowelItem;

import java.util.function.BiConsumer;

import static net.minecraft.world.level.storage.loot.entries.EmptyLootItem.emptyItem;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;

public class TrowelLootGen implements LootTableSubProvider{
	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p){
		p.accept(TrowelItem.TROWEL_LOOT_TABLE, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0f))
						.add(lootTableItem(Items.WHEAT_SEEDS).setWeight(33))
						.add(lootTableItem(Bait.WORM).setWeight(33))
						.add(emptyItem().setWeight(33))
						.add(lootTableItem(Bait.GOLDEN_WORM).setWeight(1))));
	}
}
