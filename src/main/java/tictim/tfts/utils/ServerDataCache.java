package tictim.tfts.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber(modid = TFTSMod.MODID)
public final class ServerDataCache<IN, T>{
	private final BiFunction<IN, MinecraftServer, T> computer;
	private final Map<IN, T> cache = new Object2ObjectOpenHashMap<>();

	public ServerDataCache(@NotNull BiFunction<@NotNull IN, @NotNull MinecraftServer, @NotNull T> computer){
		this.computer = Objects.requireNonNull(computer, "computer == null");
		instances.add(new WeakReference<>(this));
	}

	@Nullable public T get(@NotNull IN in){
		return getOrElse(in, null);
	}

	@Nullable public T getOrElse(@NotNull IN in, @Nullable T onInvalid){
		MinecraftServer server = currentServer;
		if(server==null) return onInvalid;
		return this.cache.computeIfAbsent(in, in_ -> computer.apply(in_, server));
	}

	public void clearCache(){
		this.cache.clear();
	}

	private static final List<WeakReference<ServerDataCache<?, ?>>> instances = new ArrayList<>();
	@Nullable private static MinecraftServer currentServer;

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent event){
		clearInstanceCache();
		currentServer = event.getServer();
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event){
		if(event.getPlayer()!=null) return;
		clearInstanceCache();
		currentServer = event.getPlayerList().getServer();
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event){
		currentServer = null;
		clearInstanceCache();
	}

	private static void clearInstanceCache(){
		for(int i = 0; i<instances.size(); i++){
			ServerDataCache<?, ?> cache = instances.get(i).get();
			if(cache==null) instances.remove(i--);
			else cache.clearCache();
		}
	}
}
