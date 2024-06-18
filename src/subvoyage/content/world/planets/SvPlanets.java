package subvoyage.content.world.planets;

import arc.graphics.*;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import subvoyage.content.blocks.*;
import subvoyage.content.world.*;
import subvoyage.content.world.items.*;
import subvoyage.content.world.planets.c.AtlacianPlanet;

import static mindustry.content.Planets.*;

public class SvPlanets{
    public static Planet atlacian;

    public static void load() {

        serpulo.orbitSpacing = 3f;
        atlacian = new AtlacianPlanet("atlacian", serpulo, 0.6f, 2) {{
            generator = new AtlacianPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Pal.spore).mul(0.9f).a(0.25f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.9f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.2f), 2, 0.45f, 1f, 0.41f),
                    new HexSkyMesh(this, 13, 0.3f, 0.18f, 4, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.25f), 4, 0.6f, 2f, 0.41f)
            );

            defaultCore = SvBlocks.corePuffer;
            unlockedOnLand.add(SvBlocks.corePuffer);

            sectorSeed = 6;
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = true;
            //allowLaunchSchematics = true;
            //enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;

            //doesn't play well with configs
            prebuildBase = false;
            ruleSetter = r -> {
                r.waveTeam = Team.malis;
                r.placeRangeCheck = false;
                r.loadout = Seq.with();
                r.enemyCoreBuildRadius = 100f;
                r.showSpawns = true;
                r.fog = true;
                r.staticFog = true;

            };

            iconColor = Color.valueOf("7286AD");
            atmosphereColor = Color.valueOf("EAC7DE");
            atmosphereRadIn = 0.03f;
            atmosphereRadOut = 0.1f;
            startSector = 7;
            defaultEnv = Environment.legarytic | Env.terrestrial;


            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("4F4CB5");
            hiddenItems.addAll(Items.erekirItems).addAll(Items.serpuloItems)
                    .removeAll(SvItems.atlacianItems);
        }};

        serpulo.hiddenItems.addAll(SvItems.atlacianItems);
        erekir.hiddenItems.addAll(SvItems.atlacianItems);
    }
}
