package tictim.tfts.contents;

import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import tictim.tfts.contents.entity.TFTSHook;
import tictim.tfts.utils.A;

import static tictim.tfts.TFTSMod.MODID;
import static tictim.tfts.contents.TFTSRegistries.ENTITIES;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TFTSEntities{
	private TFTSEntities(){}

	public static void init(){}

	public static final RegistryObject<EntityType<TFTSHook>> HOOK = ENTITIES.register("hook",
			() -> EntityType.Builder.<TFTSHook>of(TFTSHook::new, MobCategory.MISC)
					.noSave().noSummon()
					.sized(0.25F, 0.25F)
					.clientTrackingRange(4)
					.updateInterval(5)
					.build(A.stfu()));

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event){
		event.registerEntityRenderer(TFTSEntities.HOOK.get(), FishingHookRenderer::new);
	}
}
