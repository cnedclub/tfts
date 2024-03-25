package tictim.tfts.contents.recipe;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface RecipeResult<Context, ResultProcessor, Result>{
	@NotNull Result process(@NotNull Context context, @NotNull ResultProcessor processor);
}
