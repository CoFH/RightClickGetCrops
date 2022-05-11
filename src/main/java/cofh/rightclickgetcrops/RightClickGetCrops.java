package cofh.rightclickgetcrops;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod ("right_click_get_crops")
public class RightClickGetCrops {

    public RightClickGetCrops() {

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        RCGCConfig.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        RCGCEvents.init();
    }

}
