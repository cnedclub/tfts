package tictim.tfts.contents.fish;

public interface BaitTypes{
	// plant supercategory represents, well, plants

	BaitType ALL_PLANTS = BaitType.of("plant");

	BaitType APPLE = BaitType.of("plant/fruit/apple");
	BaitType SWEET_BERRY = BaitType.of("plant/fruit/sweet_berry");
	BaitType GLOW_BERRY = BaitType.of("plant/fruit/glow_berry");
	BaitType MELON = BaitType.of("plant/fruit/melon");
	BaitType PUMPKIN = BaitType.of("plant/fruit/pumpkin");

	BaitType POTATO = BaitType.of("plant/vegetable/potato");
	BaitType POISONOUS_POTATO = BaitType.of("plant/vegetable/potato/poisonous");
	BaitType BEETROOT = BaitType.of("plant/vegetable/beetroot");
	BaitType CARROT = BaitType.of("plant/vegetable/carrot");

	BaitType SEED = BaitType.of("plant/seed");
	BaitType FLOWER = BaitType.of("plant/flower");

	// mushroom supercategory represents mushrooms

	BaitType ALL_MUSHROOMS = BaitType.of("mushroom");

	BaitType BROWN_MUSHROOM = BaitType.of("mushroom/overworld/brown");
	BaitType RED_MUSHROOM = BaitType.of("mushroom/overworld/red");

	// meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat. meat.

	BaitType ALL_MEATS = BaitType.of("meat");

	BaitType INSECT_MEAT = BaitType.of("meat/insect");
	BaitType WORM_MEAT = BaitType.of("meat/insect/worm");
	BaitType FISH_MEAT = BaitType.of("meat/fish");

	// mineral supercategory is... you know what it is.

	BaitType ALL_MINERALS = BaitType.of("mineral");

	BaitType GOLD = BaitType.of("mineral/gold");

	// monster supercategory represents all kinds of monster drops

	BaitType ALL_MONSTER_ITEMS = BaitType.of("monster");

	BaitType ROTTEN_FLESH = BaitType.of("monster/zombie/rotten_flesh");
	BaitType SPIDER_EYE = BaitType.of("monster/spider/eye");
	BaitType FERMENTED_SPIDER_EYE = BaitType.of("monster/spider/eye/fermented");
	BaitType BONE = BaitType.of("monster/skeleton/bone");

	// food supercategory represents processed foods, often for human consumption but not necessarily

	BaitType ALL_FOODS = BaitType.of("food");

	BaitType BREAD = BaitType.of("food/bread");
	BaitType CAKE = BaitType.of("food/cake");
	BaitType COOKIE = BaitType.of("food/cookie");
	BaitType HONEY = BaitType.of("food/honey");
	BaitType SUGAR = BaitType.of("foot/sugar");

	// exotic baits, or "otherworldly" baits has its own supercategory

	BaitType ALL_NETHER_MUSHROOMS = BaitType.of("nether/mushroom");

	BaitType CRIMSON_FUNGUS = BaitType.of("nether/mushroom/crimson");
	BaitType WARPED_FUNGUS = BaitType.of("nether/mushroom/warped");
	BaitType NETHER_WART = BaitType.of("nether/mushroom/wart"); // i think nether warts count as fungi??????
}
