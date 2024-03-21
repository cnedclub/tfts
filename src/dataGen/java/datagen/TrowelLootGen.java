package datagen;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import tictim.tfts.TFTSMod;

import java.util.function.BiConsumer;

public class TrowelLootGen implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p) {
        p.accept(new ResourceLocation(TFTSMod.MODID, "items/trowel"), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f))
                .add(LootItem.lootTableItem(Items.DIRT).setWeight(33))
                .add(LootItem.lootTableItem(Items.ACACIA_BOAT).setWeight(33))
                .add(EmptyLootItem.emptyItem().setWeight(33))
                .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(1))));
    }
}
