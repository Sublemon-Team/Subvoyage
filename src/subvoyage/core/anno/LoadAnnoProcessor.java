package subvoyage.core.anno;


import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.world.Block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

public class LoadAnnoProcessor {

    public static void begin(String modId) {
        for (Block block : Vars.content.blocks()) {
            if(block == null) return;
            Arrays.stream(block.getClass().getFields()).filter(e -> e.isAnnotationPresent(LoadAnno.class))
                    .forEach(ann -> {
                        LoadAnno anno =  ann.getAnnotation(LoadAnno.class);
                        String id = anno.value().replace("@",block.name);
                        String def = anno.def().replace("@",block.name);
                        if(def.isEmpty()) def = id;
                        Log.debug("[LoadAnnoProcessor]: Loading Region for "
                                +block.name.substring(modId.length()+1).toUpperCase()
                                +": val - ["+id+"] def - ["+def+"]");
                        try {
                            ann.set(block, Core.atlas.find(id,def));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Target(value=ElementType.FIELD)
    @Retention(value= RetentionPolicy.RUNTIME)
    public static @interface LoadAnno {
        String value();
        String def() default "";
    }
}
