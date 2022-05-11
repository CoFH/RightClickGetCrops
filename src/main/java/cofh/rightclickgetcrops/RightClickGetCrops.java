package cofh.rightclickgetcrops;

import net.minecraftforge.fml.common.Mod;

@Mod ("right_click_get_crops")
public class RightClickGetCrops {

    public RightClickGetCrops() {

        RCGCConfig.register();
        RCGCEvents.init();
    }

}
