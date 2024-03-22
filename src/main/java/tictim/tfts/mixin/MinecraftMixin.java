package tictim.tfts.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.tfts.contents.fish.AnglingUtils;
import tictim.tfts.net.TFTSNet;
import tictim.tfts.net.messages.OpenCurioBaitBoxScreenMsg;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin{
	@Shadow private MultiPlayerGameMode gameMode;
	@Shadow private LocalPlayer player;
	@Shadow private Options options;

	@Inject(method = "handleKeybinds", at = @At("HEAD"))
	public void tfts$onHandleKeybinds(CallbackInfo i){
		if(player.isSpectator()||
				AnglingUtils.getFishingHand(player)==null||
				!options.keyShift.isDown()) return;
		if(options.keyInventory.consumeClick()){
			TFTSNet.NET.sendToServer(OpenCurioBaitBoxScreenMsg.get());
			//noinspection StatementWithEmptyBody
			while(options.keyInventory.consumeClick()) ;
		}
	}
}
