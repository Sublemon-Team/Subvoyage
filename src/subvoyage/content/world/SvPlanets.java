package subvoyage.content.world;

import arc.func.Cons;
import arc.graphics.*;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.AsteroidGenerator;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.meta.*;
import subvoyage.content.block.SvEnvironment;
import subvoyage.content.block.SvStorage;
import subvoyage.content.other.SvTeam;
import subvoyage.core.SvSettings;
import subvoyage.core.draw.SvPal;
import subvoyage.core.draw.mesh.AuroraMesh;
import subvoyage.type.world.*;

import static mindustry.content.Planets.*;

public class SvPlanets{
    public static Planet atlacian, thalia, mara;

    public static void load() {
        atlacian = new AtlacianPlanetType("atlacian", sun, 1f, 3) {{
            icon = "atlacian";
            generator = new AtlacianPlanetGenerator();
            Vec3 ringPos = new Vec3(0,-1f,0).rotate(Vec3.X, 5);

            meshLoader = () -> new MultiMesh(
                    new HexMesh(this, Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8))
            );

            atmosphereMesh = () -> new MultiMesh(
                    new AuroraMesh(this, (int) (160/0.2f*((Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8) -5)/3f*0.8f+0.2f)), 1.9f/0.6f, 0.15f, -3f, ringPos),
                    new AuroraMesh(this, (int) (120/0.2f*((Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8) -5)/3f*0.8f+0.2f)), 1.2f/0.6f, 1.05f, 1f, ringPos),
                    new AuroraMesh(this, (int) (160/0.2f*((Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8) -5)/3f*0.8f+0.2f)), -1.9f/0.6f, 0.15f, -5f, ringPos),
                    new AuroraMesh(this, (int) (120/0.2f*((Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8) -5)/3f*0.8f+0.2f)), -1.2f/0.6f, 1.05f, 3f, ringPos)
            );

            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.9f, 0.25f, Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8), SvPal.atlacianAtmosphere.cpy().lerp(Color.white,0.3f).a(0.1f), 3, 0.42f, 0.8f, 0.43f),
                    new HexSkyMesh(this, 3, 0.5f, 0.27f, Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8), SvPal.atlacianAtmosphere.cpy().lerp(Color.white,0.5f).a(0.2f), 3, 0.42f, 1.2f, 0.45f)
            );

            defaultCore = SvStorage.corePuffer;
            unlockedOnLand.add(SvStorage.corePuffer);

            allowLaunchToNumbered = false;

            sectorSeed = 6;

            allowSectorInvasion = false;
            allowLaunchSchematics = false;
            //enemyCoreSpawnReplace = true;
            allowLaunchLoadout = false;
            updateLighting = false;

            //doesn't play well with configs
            prebuildBase = true;
            ruleSetter = r -> {
                r.waveTeam = Team.malis;
                r.defaultTeam = SvTeam.melius;

                r.showSpawns = true;
                r.fog = true;
                r.staticFog = true;
                r.lighting = false;

                r.coreIncinerates = true;
                r.onlyDepositCore = true;
            };

            iconColor = Color.white;

            lightColor = SvPal.tugSheetLightish.cpy().a(0.01f);
            atmosphereColor = SvPal.atlacianAtmosphere.cpy().value(0.5f);
            atmosphereRadIn = 0.12f;
            atmosphereRadOut = 0.5f;

            minZoom = 0.2f;
            camRadius = 0.5f;
            startSector = 221;
            defaultEnv = Env.terrestrial;

            clearSectorOnLose = true;

            alwaysUnlocked = true;
            landCloudColor = SvPal.atlacianLandCloud;

            enemyBuildSpeedMultiplier = 0.4f;

            campaignRuleDefaults.fog = true;
            campaignRuleDefaults.showSpawns = true;
        }};

        mara = makeAsteroid("mara", atlacian, SvEnvironment.sodilateStone, Blocks.crystalFloor,
                4, 0.3f, 3, 1f, gen -> {

                });

        thalia = makeAsteroid("thalia", atlacian, SvEnvironment.darkLegartyteStone, SvEnvironment.legaryellowStone,
                8, 0.3f, 2, 1f, gen -> {

                });
    }

    private static Planet makeAsteroid(String name, Planet parent, final Block base, final Block tint, final int seed, final float tintThresh, final int pieces, final float scale, final Cons<AsteroidGenerator> cgen) {
        return new Planet(name, parent, 0.12F) {
            {
                this.hasAtmosphere = false;
                this.updateLighting = false;
                this.sectors.add(new Sector(this, PlanetGrid.Ptile.empty));
                this.camRadius = 0.68F * scale;
                this.minZoom = 0.6F;
                this.drawOrbit = false;
                this.accessible = false;
                this.clipRadius = 2.0F;
                this.defaultEnv = 2;
                this.icon = "commandRally";
                this.generator = new AsteroidGenerator();
                cgen.get((AsteroidGenerator)this.generator);
                this.meshLoader = () -> {
                    this.iconColor = tint.mapColor;
                    Color tinted = tint.mapColor.cpy().a(1.0F - tint.mapColor.a);
                    Seq<GenericMesh> meshes = new Seq();
                    Color color = base.mapColor;
                    Rand rand = new Rand((long)(this.id + 2));
                    meshes.add(new NoiseMesh(this, seed, Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8) / 2, this.radius, 2, 0.55F, 0.45F, 14.0F, color, tinted, 3, 0.6F, 0.38F, tintThresh));

                    for(int j = 0; j < pieces; ++j) {
                        meshes.add(new MatMesh(new NoiseMesh(this, seed + j + 1, Mathf.clamp(SvSettings.iDef("planet-divisions",6),5,8) / 3, 0.022F + rand.random(0.039F) * scale, 2, 0.6F, 0.38F, 20.0F, color, tinted, 3, 0.6F, 0.38F, tintThresh), (new Mat3D()).setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44F, 1.4F) * scale))));
                    }

                    return new MultiMesh((GenericMesh[])meshes.toArray(GenericMesh.class));
                };
            }
        };
    }
}
