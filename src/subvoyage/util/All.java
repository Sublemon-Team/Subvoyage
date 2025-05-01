package subvoyage.util;

public class All {
    public static void unsafe(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {

        }
    }
    public static void unsafe(boolean bool, Runnable runnable) {
        try {
            if(bool) runnable.run();
        } catch (Throwable ignored) {

        }
    }
}
