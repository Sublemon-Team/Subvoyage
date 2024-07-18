package subvoyage;

public class Mathg {
    /**
     * @param in [0,1]
     * @return out [-1,1]
     */
    public static float linSin(float in) {
        in %= 1f;
        return (in > 0.5f ? 1f-in : in)*4f-1f;
    }
}
