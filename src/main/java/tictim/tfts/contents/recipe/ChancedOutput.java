package tictim.tfts.contents.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ChancedOutput(@NotNull ItemStack stack, double chance){
	public ChancedOutput{
		Objects.requireNonNull(stack, "stack == null");
	}

	@NotNull public ItemStack copyNAmount(int amount, @NotNull RandomSource random){
		if(amount<=0||!(this.chance>0)) return ItemStack.EMPTY;
		if(this.chance>=1) return this.stack.copyWithCount(this.stack.getCount()*amount);
		int c = 0;
		for(int i = 0; i<amount; i++){
			if(random.nextDouble()<this.chance) c++;
		}
		return this.stack.copyWithCount(this.stack.getCount()*amount);
	}

	@NotNull public static ChancedOutput fromJson(@NotNull JsonObject json){
		ItemStack stack = CraftingHelper.getItemStack(json, true, true);
		double chance = GsonHelper.getAsDouble(json, "chance", 1);
		if(!Double.isFinite(chance)||chance<0||chance>1){
			throw new JsonParseException("Invalid chance value: "+chance);
		}
		return new ChancedOutput(stack, chance);
	}

	@NotNull public static ChancedOutput fromNetwork(@NotNull FriendlyByteBuf buf){
		return new ChancedOutput(buf.readItem(), buf.readDouble());
	}

	@NotNull public JsonObject toJson(){
		JsonObject o = new JsonObject();
		o.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.stack.getItem()))
				.toString());
		o.addProperty("count", this.stack.getCount());
		if(this.chance!=1) o.addProperty("chance", this.chance);
		return o;
	}

	public void toNetwork(@NotNull FriendlyByteBuf buf){
		buf.writeItemStack(this.stack, false);
		buf.writeDouble(this.chance);
	}
}
