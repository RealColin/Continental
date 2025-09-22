package realcolin.continental;

import realcolin.continental.platform.Services;


public class Continental {
    public static void init() {
        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Continental loaded on {} in a {} environment.", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        }
    }
}