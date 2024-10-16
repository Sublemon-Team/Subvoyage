package subvoyage;

import javax.net.ssl.SSLProtocolException;

public class BalanceStates {
    public static final float
    HU = 400f, //HEALTH UNIT

    OFFENSE_HU = HU*1.125f,
    DEFENSE_HU = HU*1.5f,
    SPECIAL_HU = HU*1.05f,

    //UNITS TIER 1
    HELIO_T1_HU = OFFENSE_HU*1f,

    HYDRO_T1_HU = DEFENSE_HU*1f,

    ROVER_T1_HU = SPECIAL_HU*1f,

    //UNITS TIER2
    HELIO_T2_HU = OFFENSE_HU*2.8f,

    HYDRO_T2_HU = DEFENSE_HU*3f,

    ROVER_T2_HU = SPECIAL_HU*2.5f,

    //UNITS TIER3
    HELIO_T3_HU = OFFENSE_HU*4.5f,

    HYDRO_T3_HU = DEFENSE_HU*6f,

    ROVER_T3_HU = SPECIAL_HU*4.25f,

    //UNITS TIER4
    HELIO_T4_HU = OFFENSE_HU*8.2f,

    HYDRO_T4_HU = DEFENSE_HU*12f,

    ROVER_T4_HU = SPECIAL_HU*8.4f,

    //UNITS TIER5
    HELIO_T5_HU = OFFENSE_HU*14f,

    HYDRO_T5_HU = DEFENSE_HU*20f,

    ROVER_T5_HU = SPECIAL_HU*17f
    ;
}
