package net.cu5tmtp.GregECore.gregstuff.GregMachines.managers;

public class LearningAcceleratedEBFManager {

    //start from int[1] to better illustrate tier levels
    private static final int[] tiers = new int[6];

    public static void setTierBoosts(int... tierValues) {
        System.arraycopy(tierValues, 0, tiers, 1, Math.min(tierValues.length, 5));
    }

    public static void addTierProgress(int tierIndex, int value) {
        if (tierIndex >= 1 && tierIndex <= 5) {
            tiers[tierIndex] += value;
        }
    }

    public static double getEnergyBoost() {
        double mod = 0;
        if (tiers[1] > 15000) mod += 0.5;
        if (tiers[2] > 10000) mod += 0.3;
        if (tiers[3] > 4000)  mod += 0.15;
        return 1 - mod;
    }

    public static double getSpeedBoost() {
        double mod = 0;
        if (tiers[2] > 10000) mod += 0.5;
        if (tiers[3] > 4000)  mod += 0.3;
        if (tiers[4] > 2000)  mod += 0.15;
        return 1 - mod;
    }

    public static int getParallelBoost() {
        int mod = 0;
        if (tiers[3] > 4000) mod += 16;
        if (tiers[4] > 2000) mod += 80;
        if (tiers[5] > 1000) mod += 160;
        return (mod == 0) ? 1 : mod;
    }
}
