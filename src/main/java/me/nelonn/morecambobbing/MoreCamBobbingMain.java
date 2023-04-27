package me.nelonn.morecambobbing;

import me.nelonn.morecambobbing.util.data.LivingEntityAnimatorRegistry;
import me.nelonn.morecambobbing.util.data.TimelineGroupDataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoreCamBobbingMain implements ModInitializer {
	public static final String MOD_ID = "morecambobbing";


	public static Logger LOGGER = LogManager.getLogger();

	public static final LivingEntityAnimatorRegistry ENTITY_ANIMATORS = new LivingEntityAnimatorRegistry();
	public static Entity debugEntity;


	public static void onClientInit() {
		registerTimelineGroupLoader();
	}


	private static void registerTimelineGroupLoader(){
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new TimelineGroupDataLoader());
	}

	@Override
	public void onInitialize() {
		onClientInit();
	}

}