package net.cu5tmtp.GregECore.gregstuff.GregMachines.managers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class DysonSwarmManager extends SavedData {
    private double totalSails = 0;

    public static DysonSwarmManager create() {
        return new DysonSwarmManager();
    }

    public static DysonSwarmManager load(CompoundTag tag) {
        DysonSwarmManager data = create();
        data.totalSails = tag.getDouble("TotalSails");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putDouble("TotalSails", totalSails);
        return tag;
    }

    public double getTotalSails() {
        return totalSails;
    }

    public void addSail() {
        this.totalSails += 10;
        this.setDirty();
    }

    public void addSail50() {
        this.totalSails += 50;
        this.setDirty();
    }

    public void addSail150() {
        this.totalSails += 150;
        this.setDirty();
    }

    public double getBoost() {
        if (totalSails > 1000000) return 250;
        if (totalSails > 200000) return 75;
        if (totalSails > 75000) return 10;
        if (totalSails > 500) return 1;
        return 0;
    }

    public static DysonSwarmManager get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                DysonSwarmManager::load,
                DysonSwarmManager::create,
                "greg_ecore_dyson_swarm"
        );
    }
}