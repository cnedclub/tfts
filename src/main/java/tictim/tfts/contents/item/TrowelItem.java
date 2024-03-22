package tictim.tfts.contents.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.TFTSMod;

import java.util.List;

public class TrowelItem extends ShovelItem{
	public static final ResourceLocation TROWEL_LOOT_TABLE = TFTSMod.id("items/trowel");

	public static final Tier IRON_TROWEL_TIER = new ForgeTier(2, 250, 4, 1, 14,
			BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(Items.IRON_INGOT));

	public TrowelItem(Properties properties){
		super(IRON_TROWEL_TIER, 1.5f, -3, properties);
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
			}else{
				// TODO some particles would be nice
			}
			level.playSound(context.getPlayer(), blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1, 1);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ToolAction toolAction){
		return toolAction!=ToolActions.SHOVEL_FLATTEN&&super.canPerformAction(stack, toolAction);
	}
}
