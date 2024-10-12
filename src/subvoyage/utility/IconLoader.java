package subvoyage.utility;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.ui.Fonts;
import subvoyage.Subvoyage;

import java.util.Scanner;

public class IconLoader {
    public static void loadIcons() {
        Seq<Font> fonts = Seq.with(Fonts.def, Fonts.outline);
        Texture uitex = Core.atlas.find("logo").texture;
        int size = (int)(Fonts.def.getData().lineHeight/Fonts.def.getData().scaleY);
        try(Scanner scan = new Scanner(Vars.tree.get("icons/"+ Subvoyage.ID +"-icons.properties").read(512))){
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] split = line.split("=");
                String[] nametex = split[1].split("\\|");
                String character = split[0], texture = nametex[1];
                int ch = Integer.parseInt(character);
                TextureRegion region = Core.atlas.find(texture);

                Log.info("Loading subvoyage glyph: "+ch+" - "+texture);

                if(region.texture != uitex){
                    Log.info("Failed subvoyage glyph (wrong atlas): "+ch+" - "+texture);
                    continue;
                }

                //unicodeIcons.put(nametex[0], ch);
                //stringIcons.put(nametex[0], ((char)ch) + "");

                Vec2 out = Scaling.fit.apply(region.width, region.height, size, size);

                Font.Glyph glyph = new Font.Glyph();
                glyph.id = ch;
                glyph.srcX = 0;
                glyph.srcY = 0;
                glyph.width = (int)out.x;
                glyph.height = (int)out.y;
                glyph.u = region.u;
                glyph.v = region.v2;
                glyph.u2 = region.u2;
                glyph.v2 = region.v;
                glyph.xoffset = 0;
                glyph.yoffset = -size;
                glyph.xadvance = size;
                glyph.kerning = null;
                glyph.fixedWidth = true;
                glyph.page = 0;
                Log.info("Loaded subvoyage glyph: "+ch+" - "+texture);
                fonts.each(f -> f.getData().setGlyph(ch, glyph));
            }
        }
    }
}
