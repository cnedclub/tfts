package tictim.tfts.contents.fish.condition;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.AnglingContext;

import java.util.Locale;

public enum LightCondition implements FishCondition<LightCondition> {

    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), ELEVEN(11), TWELEVE(12), THIRTEEN(13), FOURTEEN(14), FIFTEEN(15);

    private final FishConditionType<LightCondition> type = new FishConditionType<>(Codec.unit(this));

    private final int light;

    LightCondition(int i) {
        light = i;
    }

    @Override
    public FishConditionType<LightCondition> type() {
        return this.type;
    }

    @Override
    public boolean matches(@NotNull AnglingContext context) {
        Level level = context.level;
        //From Monster#isDarkEnoughToSpawn
        BlockPos pos = context.pos;
        if (level.getBrightness(LightLayer.SKY, pos) > level.random.nextInt(32)) {
            return false;
        } else {
            DimensionType dimensiontype = level.dimensionType();
            int i = this.light;
            if (i < 15 && level.getBrightness(LightLayer.BLOCK, pos) > i) {
                return false;
            } else {
                int j = level.isThundering() ? level.getMaxLocalRawBrightness(pos, 10) : level.getMaxLocalRawBrightness(pos);
                return j <= dimensiontype.monsterSpawnLightTest().sample(level.random);
            }
        }
    }

    private static boolean init;

    public static void register() {
        if (init) throw new IllegalStateException("fuck you");
        else init = true;

        for (LightCondition c : values()) {
            TFTSRegistries.FISH_CONDITION_TYPES.register(c.name().toLowerCase(Locale.ROOT), c::type);
        }
    }
}
