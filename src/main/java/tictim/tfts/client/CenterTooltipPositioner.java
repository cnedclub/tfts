package tictim.tfts.client;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.EnumMap;

public final class CenterTooltipPositioner implements ClientTooltipPositioner{
	private static final EnumMap<Mode, CenterTooltipPositioner> instances = new EnumMap<>(Mode.class);

	static{
		for(Mode mode : Mode.values()) instances.put(mode, new CenterTooltipPositioner(mode));
	}

	@NotNull public static CenterTooltipPositioner centerX(){
		return get(Mode.CENTER_X);
	}
	@NotNull public static CenterTooltipPositioner centerY(){
		return get(Mode.CENTER_Y);
	}
	@NotNull public static CenterTooltipPositioner centerAll(){
		return get(Mode.CENTER_ALL);
	}
	@NotNull public static CenterTooltipPositioner get(@NotNull Mode mode){
		return instances.get(mode);
	}

	private final Mode mode;

	private CenterTooltipPositioner(@NotNull Mode mode){
		this.mode = mode;
	}

	@Override @NotNull public Vector2ic positionTooltip(int screenWidth, int screenHeight,
	                                                    int mouseX, int mouseY,
	                                                    int tooltipWidth, int tooltipHeight){
		return new Vector2i(
				mouseX+(this.mode.centerX() ? (screenWidth-tooltipWidth)/2 : 0),
				mouseY+(this.mode.centerY() ? (screenHeight-tooltipHeight)/2 : 0));
	}

	@Override public String toString(){
		return "CenterTooltipPositioner{"+
				"mode="+mode+
				'}';
	}

	public enum Mode{
		CENTER_X,
		CENTER_Y,
		CENTER_ALL;

		public boolean centerX(){
			return this!=CENTER_Y;
		}
		public boolean centerY(){
			return this!=CENTER_X;
		}
	}
}
