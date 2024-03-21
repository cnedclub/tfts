package tictim.tfts.contents.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import tictim.tfts.TFTSMod;

import java.util.List;

public class TrowelItem extends Item {
    public TrowelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        if(level.getBlockState(blockPos).getBlock() == Blocks.GRASS_BLOCK) {
            if(!level.isClientSide) {
                LootTable table = level.getServer().getLootData().getLootTable(new ResourceLocation(TFTSMod.MODID, "items/trowel"));
                LootParams lootparams = (new LootParams.Builder((ServerLevel)level)).create(LootContextParamSets.EMPTY);
                List<ItemStack> result = table.getRandomItems(lootparams);
                if(!result.isEmpty()) {
                    ItemStack stack = result.get(0);
                    ItemEntity entity = new ItemEntity(level, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), stack);
                    level.addFreshEntity(entity);
                    }
                level.setBlock(blockPos, Blocks.DIRT.defaultBlockState(), 4);
                }
            return InteractionResult.SUCCESS;
            }
        return InteractionResult.PASS;
    }
}
