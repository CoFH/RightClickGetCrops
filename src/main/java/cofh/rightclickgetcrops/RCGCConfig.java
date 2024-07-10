package cofh.rightclickgetcrops;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RCGCConfig {

    private static boolean registered = false;

    public static void register(IEventBus modEventBus) {

        if (registered) {
            return;
        }
        modEventBus.register(RCGCConfig.class);
        registered = true;

        genServerConfig();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
    }

    private RCGCConfig() {

    }

    // region CONFIG SPEC
    private static final ModConfigSpec.Builder SERVER_CONFIG = new ModConfigSpec.Builder();
    private static ModConfigSpec serverSpec;

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
    public static ModConfigSpec.ConfigValue<List<String>> cropList;
    public static Supplier<Boolean> allowList;
    public static Supplier<Boolean> replant;
    // endregion

    // region CONFIGURATION
    @SubscribeEvent
    public static void configLoading(ModConfigEvent.Loading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                break;
            case SERVER:
                refreshServerConfig();
        }
    }

    @SubscribeEvent
    public static void configReloading(ModConfigEvent.Reloading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                break;
            case SERVER:
                refreshServerConfig();
        }
    }
    // endregion
}
