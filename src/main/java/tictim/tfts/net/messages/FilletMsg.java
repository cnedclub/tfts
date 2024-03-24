package tictim.tfts.net.messages;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class FilletMsg implements Msg{
	private FilletMsg(){}

	private static final FilletMsg instance = new FilletMsg();

	public static FilletMsg get(){
		return instance;
	}

	@Override public void write(@NotNull FriendlyByteBuf buf){}
}
