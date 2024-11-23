package subvoyage.content;

import arc.graphics.*;
import arc.math.geom.Vec3;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.other.SvPal;
import subvoyage.draw.visual.AuroraMesh;
import subvoyage.world.*;
import subvoyage.world.generator.AtlacianPlanetGen;
import subvoyage.world.type.AtlacianPlanetType;

import static arc.Core.atlas;
import static mindustry.content.Planets.*;

public class SvPlanets{
    public static Planet atlacian;

    public static void load() {
        atlacian = new AtlacianPlanetType("atlacian", sun, 1f, 2) {{
            generator = new AtlacianPlanetGen();
            Vec3 ringPos = new Vec3(0,-1f,0).rotate(Vec3.X, 5);
            meshLoader = () -> new MultiMesh(
                    new HexMesh(this, 6),
                    //new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (200/0.6f), 2.6f/0.6f, -0.2f, 5f, ringPos),
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (160/0.6f), 1.9f/0.6f, 0.2f, -3f, ringPos),
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (120/0.6f), 1.2f/0.6f, 1f, 1f, ringPos),
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (160/0.6f), -1.9f/0.6f, 0.2f, -5f, ringPos),
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (120/0.6f), -1.2f/0.6f, 1f, 3f, ringPos)
                    //new AuroraMesh(atlas.find("subvoyage-aurora2"), this, 20, 2.2f, 0.4f, ringPos)
            );

            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Pal.spore).mul(0.9f).a(0.15f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.9f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.1f), 2, 0.45f, 1f, 0.41f),
                    new HexSkyMesh(this, 13, 0.3f, 0.18f, 4, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.15f), 4, 0.6f, 2f, 0.41f)
            );


            defaultCore = SvBlocks.corePuffer;
            unlockedOnLand.add(SvBlocks.corePuffer);

            sectorSeed = 6;

            allowSectorInvasion = false;
            allowWaveSimulation = true;
            allowLaunchSchematics = false;
            //enemyCoreSpawnReplace = true;
            allowLaunchLoadout = false;

            //doesn't play well with configs
            prebuildBase = true;
            ruleSetter = r -> {
                r.waveTeam = Team.malis;
                r.defaultTeam = SvTeam.melius;
                r.placeRangeCheck = false;
                r.enemyCoreBuildRadius = 300f;
                r.showSpawns = true;
                r.fog = true;
                r.staticFog = true;
            };

            iconColor = SvPal.atlacianIcon.cpy().lerp(SvPal.legartyte,0.5f);
            lightColor = SvPal.tugSheetLightish.cpy().a(0.01f);
            atmosphereColor = SvPal.atlacianAtmosphere.cpy().a(0.02f);
            atmosphereRadIn = 0.03f;
            atmosphereRadOut = 0.1f;

            minZoom = 0.2f;
            camRadius = 0.5f;
            startSector = 13;
            defaultEnv = Environment.legarytic | Env.terrestrial;

            alwaysUnlocked = true;
            landCloudColor = SvPal.atlacianLandCloud;
            hiddenItems.addAll(Items.erekirItems).addAll(Items.serpuloItems)
                    .removeAll(SvItems.atlacianItems);
        }};

        serpulo.hiddenItems.addAll(SvItems.atlacianItems);
        erekir.hiddenItems.addAll(SvItems.atlacianItems);
        System.out.println("sectors: "+atlacian.sectors.size);
    }
}
