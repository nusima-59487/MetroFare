package com.hasunemiku2015.metrofare.Gate.People;

import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Objects;

public class GateUtil {
    protected static void setBlock(Sign sign) {
        if (findSignBelow(sign.getBlock()) == null) {
            return;
        }
        String[] data = Objects.requireNonNull(findSignBelow(sign.getBlock())).getLines();
        if (data[0].equalsIgnoreCase("")) {
            return;
        }

        String[] blk1 = data[0].split(",");
        String[] blk2 = data[1].equals("") ? new String[3] : data[1].split(",");
        if (blk1.length != 3 || blk2.length != 3) return;

        Location loc = sign.getLocation();
        addDirectionalOffset(sign, blk1, data[2], data[3], loc);

        if (!Arrays.equals(blk2, new String[3])) {
            loc = sign.getLocation();
            addDirectionalOffset(sign, blk2, data[2], data[3], loc);
        }
    }

    private static Sign findSignBelow(Block b) {
        World w = b.getWorld();
        Location l = b.getLocation();
        for (int i = 1; i < 4; i++) {
            double y = l.getY();
            l.setY(--y);
            if (w.getBlockAt(l).getState() instanceof Sign) {
                return (Sign) w.getBlockAt(l).getState();
            }
        }
        return null;
    }

    private static void addDirectionalOffset(Sign sign, String[] blkData, String baseState, String transientState, Location l0) {
        try {
            l0.add(getDirectionalOffset(sign, Integer.parseInt(blkData[0]), Integer.parseInt(blkData[1]), Integer.parseInt(blkData[2])));
        } catch (NumberFormatException e) {
            return;
        }

        setBlockNBT(transientState, l0);
        Bukkit.getScheduler().runTaskLater(MTFA.plugin, () -> setBlockNBT(baseState, l0), MFConfig.INSTANCE.getOpenTime());
    }

    private static Vector getDirectionalOffset(Sign sign, int breath, int height, int depth) {
        WallSign ws = (WallSign) sign.getBlockData();

        switch (ws.getFacing()) {
            case EAST:
                return new Vector(-depth, height, -breath);
            case WEST:
                return new Vector(depth, height, breath);
            case SOUTH:
                return new Vector(breath, height, -depth);
            case NORTH:
                return new Vector(-breath, height, depth);
        }
        return new Vector(0, 0, 0);
    }

    private static void setBlockNBT(String blockStr, Location loc) {
        if (blockStr.contains("[") && blockStr.endsWith("]")) {
            String[] var = blockStr.split("\\[", 2);
            String type = var[0];
            BlockData nbt = Bukkit.getServer().createBlockData(Material.valueOf(type), "[" + var[1]);
            loc.getBlock().setBlockData(nbt);
        } else {
            try {
                loc.getBlock().setType(Material.valueOf(blockStr));
            } catch (Exception ignored) {
            }
        }
    }

    protected static boolean checkValid(Sign sign, String str) {
        return sign.getLine(0).equalsIgnoreCase(str);
    }

    protected static boolean validFace(Sign sign, BlockFace blockFace) {
        if (!(sign.getBlockData() instanceof WallSign)) {
            return false;
        }
        WallSign ws = (WallSign) sign.getBlockData();
        return ws.getFacing().equals(blockFace);
    }

    public static String[] parseData(String s) {
        if (s.contains(",")) {
            return s.split(",");
        } else {
            String[] out = new String[2];
            out[0] = s;
            out[1] = null;
            return out;
        }
    }
}
