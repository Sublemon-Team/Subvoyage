package subvoyage.content.sound;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import mindustry.Vars;

import java.lang.reflect.Field;

public class SvSounds {
    public static Sound flashExplosion = new Sound();
    public static Sound poweredMissileShoot = new Sound();


    public static void load(){
        for (Field field : SvSounds.class.getFields()) {
            if(field.getType() != Sound.class) continue;
            try {
                field.set(null,loadSound(field.getName()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Sound loadSound(String soundName){
        if(!Vars.headless) {
            String name = "sounds/" + soundName;
            String path = Vars.tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

            Sound sound = new Sound();

            AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundLoader.SoundParameter(sound));
            desc.errored = Throwable::printStackTrace;

            return sound;

        } else {
            return new Sound();
        }
    }
}
