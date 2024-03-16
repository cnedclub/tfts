package tictim.tfts.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import tictim.tfts.TFTSMod;

@Mod.EventBusSubscriber(modid = TFTSMod.MODID, value = Dist.CLIENT)
public final class TFTSClientEvents{
	private TFTSClientEvents(){}
}
