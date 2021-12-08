package cofh.rightclickgetcrops;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;

public class RCGCConfig {

    private static boolean registered = false;

    public static void register() {

        if (registered) {
            return;
        }
        FMLJavaModLoadingContext.get().getModEventBus().register(RCGCConfig.class);
        registered = true;

        genServerConfig();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
    }

    private RCGCConfig() {

    }

    // region CONFIG SPEC
    private static final ForgeConfigSpec.Builder SERVER_CONFIG = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec serverSpec;

    private static void genServerConfig() {

        cropList = SERVER_CONFIG
                .comment("This is the list of crops which are either allowed or denied, depending on the Allow List configuration.")
                .define("Crops", new ArrayList<>());

        allowList = SERVER_CONFIG
                .comment("If TRUE, the configuration list is an ALLOW list. If FALSE, it is a DENY list.")
                .define("Allow List", false);

        replant = SERVER_CONFIG
                .comment("If TRUE, crops will be replanted when harvested via right click. This requires a seed to drop, and is removed from the drop list.")
                .define("Attempt Replant", true);

        serverSpec = SERVER_CONFIG.build();

        refreshServerConfig();
    }

    private static void refreshServerConfig() {

    }
    // endregion

    // region VARIABLES
    public static ForgeConfigSpec.ConfigValue<List<String>> cropList;
    public static ForgeConfigSpec.BooleanValue allowList;
    public static ForgeConfigSpec.BooleanValue replant;
    // endregion

    // region CONFIGURATION
    @SubscribeEvent
    public static void configLoading(final ModConfig.Loading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                break;
            case SERVER:
                refreshServerConfig();
        }
    }

    @SubscribeEvent
    public static void configReloading(ModConfig.Reloading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                break;
            case SERVER:
                refreshServerConfig();
        }
    }
    // endregion
}
