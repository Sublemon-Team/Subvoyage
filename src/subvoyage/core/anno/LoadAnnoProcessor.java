package subvoyage.core.anno;


import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.world.Block;
import subvoyage.Subvoyage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

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

    public static void begin(Class<?> checker) {
        Arrays.stream(checker.getFields()).filter(e -> e.getType() == Block.class).forEach(b -> {
            try {
                Block block = (Block) b.get(null);
                if(block == null) return;
                Arrays.stream(block.getClass().getFields()).filter(e -> e.isAnnotationPresent(LoadAnno.class))
                        .forEach(ann -> {
                            LoadAnno anno =  ann.getAnnotation(LoadAnno.class);
                            String id = anno.value().replace("@",block.name);
                            String def = anno.def().replace("@",block.name);
                            if(def.isEmpty()) def = id;
                            Log.debug("[Subvoyage] LoadAnno: Loading Region for "
                                    +block.name.substring(Subvoyage.ID.length()+1).toUpperCase()
                                    +": val - ["+id+"] def - ["+def+"]");
                            try {
                                ann.set(block, Core.atlas.find(id,def));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
