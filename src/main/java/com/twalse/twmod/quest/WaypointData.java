package com.twalse.twmod.quest;

import net.minecraft.nbt.CompoundTag;

public class WaypointData {
    private final String id;
    private final double x;
    private final double y;
    private final double z;
    private final int color;
    private final String name;

    public WaypointData(String id, double x, double y, double z, int color, String name) {
        this.id = id != null ? id : "";
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.name = name != null ? name : "";
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
        tag.putInt("color", color);
        tag.putString("name", name);
        return tag;
    }

    public static WaypointData loadNBT(CompoundTag tag) {
        String id = tag.getString("id");
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");
        int color = tag.getInt("color");
        String name = tag.getString("name");
        return new WaypointData(id, x, y, z, color, name);
    }
}
