package subvoyage.type.block.laser.blocks;

import arc.scene.ui.Slider;
import arc.scene.ui.TextArea;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;

public class LaserSource extends LaserGenerator {
    public LaserSource(String name) {
        super(name);
        configurable = true;
    }

    public class LaserSourceBuild extends LaserGeneratorBuild {

        public float selected = 1000f;

        @Override
        public void buildConfiguration(Table table) {
            Slider sl;
            TextArea[] area = new TextArea[1];

            sl = table.slider(0f,1000f,1f,selected, (value) -> {
                selected = value;
                if(area[0] != null) area[0].setText(selected+"");
            }).growX().height(50f).get();
            table.row();
            area[0] = table.area(selected+"", (s) -> {
                try {
                    selected = Float.parseFloat(s);
                    sl.setValue(selected);
                } catch (Exception ignored){

                }
            }).growX().get();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(selected);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            selected = read.f();
        }

        @Override
        public float laser() {
            return graph().broken() ? 0f : selected;
        }
        @Override
        public float rawLaser() {
            return selected;
        }
    }
}
