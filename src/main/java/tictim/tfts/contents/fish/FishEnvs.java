package tictim.tfts.contents.fish;

import static tictim.tfts.contents.fish.PrimitiveFishEnv.*;

public interface FishEnvs{
	FishEnv ALL_OCEANS = FishEnv.of(OCEAN_SURFACE, OCEAN_DEEP,
			OCEAN_LUKEWARM_SURFACE, OCEAN_LUKEWARM_DEEP,
			OCEAN_COLD_SURFACE, OCEAN_COLD_DEEP,
			OCEAN_FROZEN_SURFACE, OCEAN_FROZEN_DEEP);

	FishEnv OCEAN = FishEnv.of(OCEAN_SURFACE, OCEAN_DEEP);
	FishEnv OCEAN_LUKEWARM = FishEnv.of(OCEAN_LUKEWARM_SURFACE, OCEAN_LUKEWARM_DEEP);
	FishEnv OCEAN_COLD = FishEnv.of(OCEAN_COLD_SURFACE, OCEAN_COLD_DEEP);
	FishEnv OCEAN_FROZEN = FishEnv.of(OCEAN_FROZEN_SURFACE, OCEAN_FROZEN_DEEP);

	FishEnv ALL_OCEAN_SURFACES = FishEnv.of(OCEAN_SURFACE, OCEAN_WARM, OCEAN_LUKEWARM_SURFACE, OCEAN_COLD_SURFACE, OCEAN_FROZEN_SURFACE);
	FishEnv ALL_DEEP_OCEANS = FishEnv.of(OCEAN_DEEP, OCEAN_LUKEWARM_DEEP, OCEAN_COLD_DEEP, OCEAN_FROZEN_DEEP);

	FishEnv ALL_WATER = FishEnv.of(ALL_OCEANS, UNKNOWN);
}
