package tictim.tfts.contents.fish;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.caps.BaitBoxInventory;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.entity.TFTSHook;

import java.util.Optional;

public final class AnglingContext{
	@NotNull public final TFTSHook hook;
	@NotNull public final ServerLevel level;
	@NotNull public final Player player;
	@NotNull public final BlockPos pos;
	@NotNull public final AnglingEnvironment environment;

	private boolean baitCached;
	@Nullable private BaitStat bait;

	public AnglingContext(
			@NotNull TFTSHook hook,
			@NotNull ServerLevel level,
			@NotNull Player player,
			@NotNull BlockPos pos,
			@NotNull AnglingEnvironment environment
	){
		this.hook = hook;
		this.level = level;
		this.player = player;
		this.pos = pos;
		this.environment = environment;
	}

	@Nullable public BaitStat bait(){
		if(!this.baitCached){
			this.baitCached = true;
			this.bait = getBaitStat();
		}
		return this.bait;
	}

	public boolean hasBait(){
		return bait()!=null;
	}

	// this is the reason why kotlin is objectively superior language
	@Nullable private BaitStat getBaitStat(){
		BaitBoxInventory baitBoxInv = AnglingUtils.getBaitBoxInventory(this.player);
		if(baitBoxInv==null) return null;
		int i = baitBoxInv.selectedIndex();
		if(i<0||i>=baitBoxInv.getInventory().getSlots()) return null;
		ItemStack stack = baitBoxInv.getInventory().getStackInSlot(i);
		if(stack.isEmpty()) return null;
		MinecraftServer server = this.level.getServer();
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
		if(key==null) return null;
		Optional<Registry<BaitStat>> o = server.registryAccess().registry(TFTSRegistries.BAIT_STAT_REGISTRY_KEY);
		if(o.isEmpty()) return null;
		Registry<BaitStat> reg = o.get();
		return reg.get(key);
	}

	public double additionalFishingPower(){
		return 0; // TODO do some shit
	}
}
