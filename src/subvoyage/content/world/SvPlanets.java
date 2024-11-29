package subvoyage.content.world;

import arc.files.Fi;
import arc.graphics.*;
import arc.math.geom.Vec3;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import subvoyage.content.block.SvStorage;
import subvoyage.content.SvItems;
import subvoyage.content.other.SvTeam;
import subvoyage.core.draw.SvPal;
import subvoyage.core.draw.mesh.AuroraMesh;
import subvoyage.type.world.*;

import static arc.Core.atlas;
import static mindustry.content.Planets.*;

public class SvPlanets{
    public static Planet atlacian;

    public static void load() {
        atlacian = new AtlacianPlanetType("atlacian", sun, 1f, 3) {{
            icon = "atlacian";
            generator = new AtlacianPlanetGenerator();
            Vec3 ringPos = new Vec3(0,-1f,0).rotate(Vec3.X, 5);
            meshLoader = () -> new MultiMesh(
                    new HexMesh(this, 6)
                    //new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (200/0.6f), 2.6f/0.6f, -0.2f, 5f, ringPos),
                    //new AuroraMesh(atlas.find("subvoyage-aurora2"), this, 20, 2.2f, 0.4f, ringPos)
            );
            atmosphereMesh = () -> new MultiMesh(
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (160/0.2f), 1.9f/0.6f, 0.2f, -3f, ringPos),
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (120/0.2f), 1.2f/0.6f, 1f, 1f, ringPos),
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (160/0.2f), -1.9f/0.6f, 0.2f, -5f, ringPos),
                    new AuroraMesh(atlas.find("subvoyage-aurora"), this, (int) (120/0.2f), -1.2f/0.6f, 1f, 3f, ringPos)
            );

            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.9f, 0.25f, 5, SvPal.atlacianAtmosphere.cpy().lerp(Color.white,0.3f).a(0.1f), 3, 0.42f, 0.8f, 0.43f),
                    new HexSkyMesh(this, 3, 0.5f, 0.27f, 5, SvPal.atlacianAtmosphere.cpy().lerp(Color.white,0.5f).a(0.2f), 3, 0.42f, 1.2f, 0.45f)
            );

            defaultCore = SvStorage.corePuffer;
            unlockedOnLand.add(SvStorage.corePuffer);

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

            iconColor = Color.white;

            lightColor = SvPal.tugSheetLightish.cpy().a(0.01f);
            atmosphereColor = SvPal.atlacianAtmosphere.cpy().value(0.5f);
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;

            minZoom = 0.2f;
            camRadius = 0.5f;
            startSector = 13;
            defaultEnv = SvEnvironment.legarytic | Env.terrestrial;

            clearSectorOnLose = true;

            alwaysUnlocked = true;
            landCloudColor = SvPal.atlacianLandCloud;
            hiddenItems.addAll(Items.erekirItems).addAll(Items.serpuloItems)
                    .removeAll(SvItems.atlacianItems);
        }};

        serpulo.hiddenItems.addAll(SvItems.atlacianItems);
        erekir.hiddenItems.addAll(SvItems.atlacianItems);
    }
}
