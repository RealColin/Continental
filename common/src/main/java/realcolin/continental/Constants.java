package realcolin.continental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MOD_ID = "continental";
	public static final String MOD_NAME = "Continental";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final double LAND_COVERAGE_MIN = 0.25;
    public static final double LAND_COVERAGE_MAX = 0.55;
    public static final double EASING_EXP = 0.7;
}