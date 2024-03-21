package tictim.tfts.net.messages;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class PrepareFishMsg implements Msg{
	private PrepareFishMsg(){}

	private static final PrepareFishMsg instance = new PrepareFishMsg();

	public static PrepareFishMsg get(){
		return instance;
	}

	@Override public void write(@NotNull FriendlyByteBuf buf){}
}
