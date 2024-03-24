package tictim.tfts.contents;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import tictim.tfts.client.screen.BaitBoxScreen;
import tictim.tfts.client.screen.FilletTableScreen;
import tictim.tfts.contents.inventory.BaitBoxMenu;
import tictim.tfts.contents.inventory.FilletTableMenu;

import static tictim.tfts.contents.TFTSRegistries.MENUS;

public final class TFTSMenus{
	private TFTSMenus(){}

	public static final RegistryObject<MenuType<BaitBoxMenu>> BAIT_BOX = MENUS.register("bait_box", () ->
			IForgeMenuType.create((id, inv, data) -> new BaitBoxMenu(TFTSMenus.BAIT_BOX.get(), id, inv, 3, data.readByte())));

	public static final RegistryObject<MenuType<FilletTableMenu>> FILLET_TABLE = MENUS.register("fillet_table", () ->
			new MenuType<>(FilletTableMenu::new, FeatureFlagSet.of()));

	@OnlyIn(Dist.CLIENT)
	@ApiStatus.Internal
	public static void registerScreens(){
		MenuScreens.register(TFTSMenus.BAIT_BOX.get(), BaitBoxScreen::new);
		MenuScreens.register(TFTSMenus.FILLET_TABLE.get(), FilletTableScreen::new);
	}
}
