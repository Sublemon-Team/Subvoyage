package subvoyage.util;

import java.util.Objects;

public class All {
    public static void unsafe(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
    }
    public static void unsafe(boolean bool, Runnable runnable) {
        try {
            if(bool) runnable.run();
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
    }

    public static boolean isDev() {
        return System.getenv("DEVELOPMENT") != null || !Objects.equals(System.getenv("DEVELOPMENT"), "false");
    }
}
