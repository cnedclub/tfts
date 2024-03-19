package tictim.tfts.net.messages;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class OpenCurioBaitBoxScreenMsg implements Msg{
	private OpenCurioBaitBoxScreenMsg(){}

	private static final OpenCurioBaitBoxScreenMsg instance = new OpenCurioBaitBoxScreenMsg();

	public static OpenCurioBaitBoxScreenMsg get(){
		return instance;
	}

	@Override public void write(@NotNull FriendlyByteBuf buf){}
}
