package tictim.tfts.contents.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.TFTSMod;

import java.util.List;

public class TrowelItem extends Item{
	public static final ResourceLocation TROWEL_LOOT_TABLE = TFTSMod.id("items/trowel");

	public TrowelItem(Properties properties){
		super(properties);
	}

	@Override @NotNull public InteractionResult useOn(UseOnContext context){
		Level level = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState state = level.getBlockState(blockPos);
		if(state.getBlock()==Blocks.GRASS_BLOCK){
			if(level instanceof ServerLevel serverLevel){
				LootTable table = serverLevel.getServer().getLootData().getLootTable(TROWEL_LOOT_TABLE);
				LootParams lootparams = (new LootParams.Builder(serverLevel)).create(LootContextParamSets.EMPTY);
				List<ItemStack> result = table.getRandomItems(lootparams);
				for(ItemStack stack : result){
					Vec3 clicked = context.getClickLocation();
					ItemEntity entity = new ItemEntity(level, clicked.x, clicked.y, clicked.z, stack);
					level.addFreshEntity(entity);
				}
				BlockState newState = Blocks.DIRT.defaultBlockState();
				level.setBlock(blockPos, newState, 4);
				level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(context.getPlayer(), newState));
				if(context.getPlayer()!=null){
					context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
							p -> p.broadcastBreakEvent(context.getHand()));
				}
				level.levelEvent(context.getPlayer(), LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(state));
			}
			level.playSound(context.getPlayer(), blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1, 1);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
