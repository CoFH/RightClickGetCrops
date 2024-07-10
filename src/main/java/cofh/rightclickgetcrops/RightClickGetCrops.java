package cofh.rightclickgetcrops;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod ("right_click_get_crops")
public class RightClickGetCrops {

    public RightClickGetCrops(ModContainer modContainer, IEventBus modEventBus) {

        modEventBus.addListener(this::commonSetup);

        RCGCConfig.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        RCGCEvents.init();
    }

}
