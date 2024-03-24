package tictim.tfts.contents.recipe;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InWorldFilletProcessor implements FilletRecipe.ResultProcessor{
	private final ServerLevel level;
	private final Vec3 inWorldPosition;
	private float experience;

	public InWorldFilletProcessor(@NotNull ServerLevel level, @NotNull Vec3 inWorldPosition){
		this.level = level;
		this.inWorldPosition = inWorldPosition;
	}

	@Override @NotNull public RandomSource random(){
		return this.level.getRandom();
	}

	public float experience(){
		return this.experience;
	}

	@Override public void addResultItem(@NotNull ItemStack stack){
		if(stack.isEmpty()) return;
		int maxStackSize = stack.getMaxStackSize();
		do{
			ItemStack stackToSpawn;
			if(maxStackSize<stack.getCount()) stackToSpawn = stack.split(maxStackSize);
			else{
				stackToSpawn = stack;
				stack = ItemStack.EMPTY;
			}
			ItemEntity entity = new ItemEntity(level, inWorldPosition.x, inWorldPosition.y, inWorldPosition.z, stackToSpawn);
			entity.setDeltaMovement(0, 0, 0);
			entity.setNoPickUpDelay();
			level.addFreshEntity(entity);
		}while(!stack.isEmpty());
	}

	@Override public void addResultExperience(float amount){
		this.experience += amount;
	}

	public void dropExperience(){
		if(this.experience<=0) return;

		int intExp = Mth.floor(this.experience);
		float frac = Mth.frac(this.experience);
		if(frac!=0&&random().nextFloat()<frac) ++intExp;

		ExperienceOrb.award(this.level, this.inWorldPosition, intExp);
	}
}
