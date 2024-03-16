package tictim.tfts;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.caps.TFTSCap;
import tictim.tfts.client.TFTSOverlay;
import tictim.tfts.contents.TFTSItems;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.entity.TFTSHook;

@Mod(TFTSMod.MODID)
public class TFTSMod{
	public static final String MODID = "tfts";
	public static final Logger LOGGER = LogManager.getLogger("Tales from the Sea");

	public TFTSMod(){
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		TFTSRegistries.init(bus);
		bus.register(TFTSMod.class);
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event){
		event.register(TFTSCap.class);
	}

	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
		event.registerAboveAll(MODID, new TFTSOverlay());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientSetup(FMLClientSetupEvent event){
		event.enqueueWork(() -> {
			ItemProperties.register(TFTSItems.FISHING_ROD.get(), new ResourceLocation("cast"),
					(stack, level, e, i) -> e instanceof Player p&&
							p.fishing instanceof TFTSHook hook&&
							hook.isItemPointingToThisHook(stack) ? 1 : 0);
		});
	}

	@NotNull
	public static ResourceLocation id(@NotNull String path){
		return new ResourceLocation(MODID, path);
	}
}
