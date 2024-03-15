package net.cloud.improved_damage.configuration;


import com.mojang.datafixers.util.Pair;
import net.cloud.cloud_util.SimpleConfig;
import net.cloud.improved_damage.ImprovedDamage;

public class ImprovedDamageConfiguration {

	public static SimpleConfig CONFIG;
	private static ModConfigProvider configs;

	public static Double PENALTY_START;
	public static Double PENALTY_MULTIPLIER;
	public static Double FLAT_REDUCTION_START;
	public static Double FLAT_REDUCTION;
	public static Double DURABLE_1_MULT;
	public static Double DURABLE_2_MULT;
	public static Double DURABLE_3_MULT;
	public static Integer DURABLE_NO_FLAT_LEVEL;
	public static Integer ENCHANTING_TABLE_MAX_LEVEL;
	public static Integer ENCHANTING_TABLE_MAX_BOOKSHELVES;
	public static Integer MENDING_DURABILITY_MULT;
	public static Boolean CRAFTING_REPAIR;
	public static Boolean ANVIL_REPAIR;

	static {
		registerConfigs();
	}
	//public static final Boolean> BROKEN_ANVIL;
	public static void registerConfigs() {
		configs = new ModConfigProvider();
		createConfigs();

		CONFIG = SimpleConfig.of(ImprovedDamage.MODID + "_config").provider(configs).request();

		assignConfigs();
	}

	private static void createConfigs() {
		configs.addKeyValuePair(new Pair<>("key.improved_damage.penalty_start", 0.25), "point at which the penalty will start (0 immediately - 1 never)");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.penalty_multiplier", 0.4), "the max multiplier of the base stat for the debuff (0 no debuff - 1 unusable item)");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.flat_start", 0.5), "point at which the flat penalty will start (0 immediately - 1 never)");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.flat_amount", 1.0), "flat reduction value (speed = this * 0.2, damage = this * 1.5, mining speed = this * 1)");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.durable_1_mult", 0.9), "the number the progression of the tool breaking will be multiplied by with durable 1");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.durable_2_mult", 0.8), "the number the progression of the tool breaking will be multiplied by with durable 2");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.durable_3_mult", 0.7), "the number the progression of the tool breaking will be multiplied by with durable 3");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.durable_no_flat_level", 2), "the level of durable at which the flat reduction will stop being applied");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.mending_durability_mult", 2), "the multiplier by which mending will multiply the original durability when applied, set to 0 to disable the changes");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.anvil_repair_enabled", true), "if anvils can be repaired with iron blocks");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.crafting_repair_enabled", false), "if items can be repaired in the crafting grid");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.enchanting_table_max_level", 40), "the max level you can reach on the third enchantment in an enchanting table");
		configs.addKeyValuePair(new Pair<>("key.improved_damage.enchanting_table_max_bookshelves", 25), "the amount of bookshelves needed to reach the enchanting_table_max_level in the enchanting table");

	}

	private static void assignConfigs() {
		PENALTY_START = CONFIG.getOrDefault("key.improved_damage.penalty_start", 0.25);
		PENALTY_MULTIPLIER = CONFIG.getOrDefault("key.improved_damage.penalty_multiplier", 0.4);
		FLAT_REDUCTION_START = CONFIG.getOrDefault("key.improved_damage.flat_start", 0.5);
		FLAT_REDUCTION = CONFIG.getOrDefault("key.improved_damage.flat_amount", 1.0);
		DURABLE_1_MULT = CONFIG.getOrDefault("key.improved_damage.durable_1_mult", 0.9);
		DURABLE_2_MULT = CONFIG.getOrDefault("key.improved_damage.durable_2_mult", 0.8);
		DURABLE_3_MULT = CONFIG.getOrDefault("key.improved_damage.durable_3_mult", 0.7);
		DURABLE_NO_FLAT_LEVEL = CONFIG.getOrDefault("key.improved_damage.durable_no_flat_level", 2);
		MENDING_DURABILITY_MULT = CONFIG.getOrDefault("key.improved_damage.mending_durability_mult", 2);
		ANVIL_REPAIR = CONFIG.getOrDefault("key.improved_damage.anvil_repair_enabled", true);
		CRAFTING_REPAIR = CONFIG.getOrDefault("key.improved_damage.crafting_repair_enabled", false);
		ENCHANTING_TABLE_MAX_LEVEL = CONFIG.getOrDefault("key.improved_damage.enchanting_table_max_level", 40);
		ENCHANTING_TABLE_MAX_BOOKSHELVES = CONFIG.getOrDefault("key.improved_damage.enchanting_table_max_bookshelves", 25);

		ImprovedDamage.LOGGER.info("All " + configs.getConfigsList().size() + " configurations have been set properly");
	}

}
