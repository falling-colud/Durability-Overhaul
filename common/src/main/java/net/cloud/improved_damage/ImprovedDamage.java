package net.cloud.improved_damage;

import net.cloud.improved_damage.init.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class ImprovedDamage
{

	//in multiblocked base one doesnt require fuel and uses x2 vanilla amount, and big one can be automated

	//TODO more configs

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "improved_damage";

	public static void init() {
		ImprovedDamageModBlocks.REGISTRY.register();
		ImprovedDamageModItems.REGISTRY.register();
		ImprovedDamageModEnchantments.REGISTRY.register();
		ImprovedDamageModMenus.REGISTRY.register();
		ImprovedDamageEvents.load();
	}

	public static final UUID damageUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID speedUUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

}
