package tictim.tfts.net.messages;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public sealed interface Msg permits OpenCurioBaitBoxScreenMsg, FilletMsg, SelectBaitBoxSlotMsg{
	void write(@NotNull FriendlyByteBuf buf);
}
