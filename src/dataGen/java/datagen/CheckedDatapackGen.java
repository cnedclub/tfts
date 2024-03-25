package datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CheckedDatapackGen extends DatapackBuiltinEntriesProvider{
	private final PackOutput output;
	private final java.util.function.Predicate<String> namespacePredicate;

	@SuppressWarnings({"DataFlowIssue", "ConstantValue"})
	public CheckedDatapackGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
	                          RegistrySetBuilder datapackEntriesBuilder, @Nullable Set<String> modIds){
		super(output, registries, datapackEntriesBuilder, modIds);
		this.output = output;
		this.namespacePredicate = modIds==null ? namespace -> true : modIds::contains;
	}

	// see: RegistriesDatapackGenerator

	@SuppressWarnings("UnstableApiUsage")
	@Override @NotNull public CompletableFuture<?> run(@NotNull CachedOutput out){
		return this.getRegistryProvider().thenCompose(a -> {
			DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, a);
			return CompletableFuture.allOf(DataPackRegistriesHooks.getDataPackRegistriesWithDimensions()
					.flatMap(data -> dumpRegistryCap(out, a, dynamicops, data).stream())
					.toArray(CompletableFuture[]::new));
		});
	}

	private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput out, HolderLookup.Provider lookupProvider,
	                                                           DynamicOps<JsonElement> dynamicOps,
	                                                           RegistryDataLoader.RegistryData<T> registryData){
		ResourceKey<? extends Registry<T>> resourcekey = registryData.key();
		return lookupProvider.lookup(resourcekey).map(a -> {
			PackOutput.PathProvider pathProvider = this.output.createPathProvider(
					PackOutput.Target.DATA_PACK,
					ForgeHooks.prefixNamespace(resourcekey.location()));
			return CompletableFuture.allOf(a.listElements()
					.filter(holder -> this.namespacePredicate.test(holder.key().location().getNamespace()))
					.map(id -> dumpValue(pathProvider.json(id.key().location()), out, dynamicOps, registryData.elementCodec(), id.value()))
					.toArray(CompletableFuture[]::new));
		});
	}

	// changed
	private static <E> CompletableFuture<?> dumpValue(Path path, CachedOutput out, DynamicOps<JsonElement> dynamicOps,
	                                                  Encoder<E> encoder, E data){
		return DataProvider.saveStable(out,
				encoder.encodeStart(dynamicOps, data)
						.mapError(err -> "Couldn't serialize element "+path+": "+err)
						.getOrThrow(false, err -> {}),
				path);
	}
}
