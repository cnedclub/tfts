package tictim.tfts.net.messages;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public record SelectBaitBoxSlotMsg(int slot) implements Msg{
	public static SelectBaitBoxSlotMsg read(@NotNull FriendlyByteBuf buf){
		return new SelectBaitBoxSlotMsg(buf.readVarInt());
	}

	@Override public void write(@NotNull FriendlyByteBuf buf){
		buf.writeVarInt(this.slot);
	}
}
