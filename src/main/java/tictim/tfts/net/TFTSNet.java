package tictim.tfts.net;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;
import tictim.tfts.angling.AnglingUtils;
import tictim.tfts.caps.BaitBoxInventory;
import tictim.tfts.contents.item.IBaitBoxItem;
import tictim.tfts.net.messages.OpenCurioBaitBoxScreenMsg;
import tictim.tfts.net.messages.SelectBaitBoxSlotMsg;
import tictim.tfts.utils.A;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.function.Supplier;

import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.angling.AnglingUtils.CURIO_BAIT_BOX_ID;

public final class TFTSNet{
	private TFTSNet(){}
	@ApiStatus.Internal
	public static void init(){}

	public static final String NETVERSION = "1.0";

	public static final SimpleChannel NET = NetworkRegistry.newSimpleChannel(
			id("master"), () -> NETVERSION, NETVERSION::equals, NETVERSION::equals);

	static{
		NET.registerMessage(0, SelectBaitBoxSlotMsg.class,
				SelectBaitBoxSlotMsg::write,
				SelectBaitBoxSlotMsg::read,
				TFTSNet::handleSelectBaitBoxSlot,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		NET.registerMessage(1, OpenCurioBaitBoxScreenMsg.class,
				OpenCurioBaitBoxScreenMsg::write,
				buf -> OpenCurioBaitBoxScreenMsg.get(),
				TFTSNet::handleOpenCurioBaitBoxScreen,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}

	private static void handleSelectBaitBoxSlot(SelectBaitBoxSlotMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context ctx = contextSupplier.get();
		ctx.setPacketHandled(true);
		ctx.enqueueWork(() -> {
			ServerPlayer sender = ctx.getSender();
			if(sender==null||sender.isSpectator()||sender.fishing!=null) return;
			BaitBoxInventory baitBox = AnglingUtils.getBaitBoxInventory(sender);
			if(baitBox==null) return;
			int slots = baitBox.getInventory().getSlots();
			if(slots<=0) return;
			int slot = msg.slot();
			while(slot<0) slot += slots;
			while(slot>=slots) slot -= slots;
			baitBox.setSelectedIndex(slot);
		});
	}

	private static void handleOpenCurioBaitBoxScreen(OpenCurioBaitBoxScreenMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context ctx = contextSupplier.get();
		ctx.setPacketHandled(true);
		ctx.enqueueWork(() -> {
			ServerPlayer sender = ctx.getSender();
			if(sender==null||sender.isSpectator()) return;

			ICuriosItemHandler curiosItemHandler = A.unwrap(CuriosApi.getCuriosInventory(sender));
			if(curiosItemHandler==null) return;
			for(SlotResult slot : curiosItemHandler.findCurios(CURIO_BAIT_BOX_ID)){
				ItemStack stack = slot.stack();
				if(stack==null||stack.isEmpty()) continue;
				if(stack.getItem() instanceof IBaitBoxItem iBaitBoxItem){
					iBaitBoxItem.openCurioScreen(sender, stack);
					return;
				}
			}
		});
	}
}
