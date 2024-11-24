package subvoyage;

public class BalanceStates {
    public static final float
    HU = 400f, //HEALTH UNIT
    DU = 200f,

    TURRET_DMG_U = DU*0.4f,

    WALL_HP_U = DU*2f, // for 1x1 wall
    WALL_LARGE_HP_U = WALL_HP_U*4f, // for 2x2 wall

    OFFENSE_HP_U = HU*1.125f,
    OFFENSE_DMG_U = DU*0.25f,
    DEFENSE_HP_U = HU*1.5f,
    DEFENSE_DMG_U = DU*0.15f,
    SPECIAL_HP_U = HU*1.05f,
    SPECIAL_DMG_U = DU*0.12f,

    //WALLS
    CLAY_WALL_HP = WALL_HP_U,
    CLAY_WALL_LARGE_HP = WALL_LARGE_HP_U,

    PHOSPHIDE_WALL_HP = WALL_HP_U*1.875f,
    PHOSPHIDE_WALL_LARGE_HP = WALL_LARGE_HP_U*1.875f,

    TUGSHEET_WALL_HP = WALL_HP_U*3.625f,
    TUGSHEET_WALL_LARGE_HP = WALL_LARGE_HP_U*3.625f,

    //TURRETS
    WHIRL_DPS = TURRET_DMG_U,
    RUPTURE_DPS = TURRET_DMG_U*1.4f,
    RESONANCE_DPS = TURRET_DMG_U*2.4f,
    CASCADE_DPS = TURRET_DMG_U*3.4f,
    SPECTRUM_DPS = TURRET_DMG_U*5.5f,
    UPSURGE_DPS = TURRET_DMG_U*4.3f,
    RESIST_DPS = TURRET_DMG_U*9.8f,

    //UNITS TIER 1
    HELIO_T1_HU = OFFENSE_HP_U * 1f,
    HELIO_T1_DPS = OFFENSE_DMG_U * 1f,

    HYDRO_T1_HU = DEFENSE_HP_U * 1f,
    HYDRO_T1_DPS = DEFENSE_DMG_U * 1f,

    ROVER_T1_HU = SPECIAL_HP_U * 1f,
    ROVER_T1_DPS = SPECIAL_DMG_U * 1f,

    //UNITS TIER2
    HELIO_T2_HU = OFFENSE_HP_U * 2.8f,
    HELIO_T2_DPS = OFFENSE_DMG_U * 2.4f,

    HYDRO_T2_HU = DEFENSE_HP_U * 3f,
    HYDRO_T2_DPS = DEFENSE_DMG_U * 1.75f,

    ROVER_T2_HU = SPECIAL_HP_U * 2.5f,
    ROVER_T2_DPS = SPECIAL_DMG_U * 1.8f,

    //UNITS TIER3
    HELIO_T3_HU = OFFENSE_HP_U * 4.5f,
    HELIO_T3_DPS = OFFENSE_DMG_U * 4.2f,

    HYDRO_T3_HU = DEFENSE_HP_U * 6f,
    HYDRO_T3_DPS = DEFENSE_DMG_U * 2.7f,

    ROVER_T3_HU = SPECIAL_HP_U * 4.25f,
    ROVER_T3_DPS = SPECIAL_DMG_U * 3f,

    //UNITS TIER4
    HELIO_T4_HU = OFFENSE_HP_U * 8.2f,
    HELIO_T4_DPS = OFFENSE_DMG_U * 8.2f,

    HYDRO_T4_HU = DEFENSE_HP_U * 12f,
    HYDRO_T4_DPS = DEFENSE_DMG_U * 10.5f,

    ROVER_T4_HU = SPECIAL_HP_U * 8.4f,
    ROVER_T4_DPS = SPECIAL_DMG_U * 8.1f,

    //UNITS TIER5
    HELIO_T5_HU = OFFENSE_HP_U * 14f,
    HELIO_T5_DPS = OFFENSE_DMG_U * 17f,

    HYDRO_T5_HU = DEFENSE_HP_U * 20f,
    HYDRO_T5_DPS = DEFENSE_DMG_U * 18f,

    ROVER_T5_HU = SPECIAL_HP_U * 17f,
    ROVER_T5_DPS = SPECIAL_DMG_U * 19f
    ;
}
