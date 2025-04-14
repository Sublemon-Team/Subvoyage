package subvoyage.util;

import subvoyage.core.SvSettings;


public class Var<T> {
    public T val;
    public Var(T initial) {
        val = initial;
    }

    public static <A> Var<A> init(A val) {
        return new Var<>(val);
    };

    public static Var<Boolean> bool() {
        return init(false);
    }
    public static Var<Boolean> boolTrue() {
        return init(true);
    }
    public static Var<Float> f() {
        return init(0f);
    }
    public static Var<Integer> stgInt(String set) {
        return init(SvSettings.i(set));
    };
    public static Var<Integer> stgInt(String set,int def) {
        return init(SvSettings.iDef(set,def));
    };

}
