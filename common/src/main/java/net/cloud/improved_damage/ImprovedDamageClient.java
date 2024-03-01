package net.cloud.improved_damage;

import net.cloud.improved_damage.init.ImprovedDamageModBlocks;
import net.cloud.improved_damage.init.ImprovedDamageModScreens;

public class ImprovedDamageClient
{
	//in multiblocked base one doesnt require fuel and uses x2 vanilla amount, and big one can be automated

	//TODO more configs

	public static void clientInit() {
		ImprovedDamageModScreens.registerScreens();
		ImprovedDamageModBlocks.clientInit();
	}

}
