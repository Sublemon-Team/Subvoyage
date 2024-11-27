package subvoyage.type.world;

import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.Texture;
import arc.util.Interval;
import arc.util.Nullable;
import mindustry.world.Tile;
import subvoyage.content.other.SvAttribute;

import static mindustry.Vars.world;


public class AtlacianMapControl {
    private float[] map;
    public int w,h;
    public int blurAmount = 1;
    public Texture textureMap;

    private @Nullable Thread heatMapThread;

    public boolean recalculate;
    public transient Interval timer = new Interval(1);

    public float getWater(Tile tile) {
        float v = tile.floor().attributes.get(SvAttribute.sodilate);
        if(!tile.block().isAir()) v = 0;
        return v;
    }

    public void update() {
        if(recalculate && timer.get(0,5f)) {
            recalcInner();
        }
    }

    public void recalc() {
        recalculate = true;
    }

    void recalcInner() {
        int i = 0;
        w = world.width(); h = world.height();
        map = new float[w*h];
        for (Tile tile : world.tiles) {
            map[i] = getWater(tile);
            i++;
        }
        textureMap = null;
        blur(blurAmount);
        add();

        recalculate = false;
    }
    private static final Object notifyThread = new Object();
    class HeatThread extends Thread {
        HeatThread(){
            super("HeatThread");
        }

        @Override
        public void run(){
            while(true){
                try{
                    synchronized(notifyThread){
                        try{
                            //wait until an event happens
                            notifyThread.wait();
                        }catch(InterruptedException e){
                            //end thread
                            return;
                        }
                    }

                    recalcInner();
                    //ignore, don't want to crash this thread
                }catch(Exception e){}
            }
        }
    }

    private void add() {
        for (int i = 0; i < map.length; i++) {
            map[i] = map[i]*2;
        }
    }

    private void blur(int iterations) {
        for (int i = 0; i < iterations; i++) blur();
    }

    private void blur() {
        float[] result = new float[map.length];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float sum = 0;
                int count = 0;

                for (int dy = -3; dy <= 3; dy++)
                    for (int dx = -3; dx <= 3; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;

                        if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                            sum += map[ny*w+nx];
                            count++;
                        }
                    }
                result[y*w+x] = sum / count;
            }
        }

        map = result;
    }

    public void stop(){
        map = null;
        textureMap = null;
        if(heatMapThread != null){
            heatMapThread.interrupt();
            heatMapThread = null;
        }
    }

    public float at(int x, int y) {
        if(map == null) return 0f;
        if(map.length < y*w+x || y*w*x < 0) return 0f;
        return map[y*w+x];
    }

    public Texture toTexture() {
        if(textureMap != null) return textureMap;
        if(map == null) return new Texture(w,h);
        Pixmap pixmap = new Pixmap(w,h);
        int i = 0;
        for (float v : map) {
            int x = i % w;
            int y = i / w;
            pixmap.set(x,y,new Color(v,0,-v,Math.abs(v)));
            i++;
        }
        textureMap = new Texture(Pixmaps.scale(pixmap,pixmap.width*8,pixmap.height*8,true));
        return textureMap;
    }
}
