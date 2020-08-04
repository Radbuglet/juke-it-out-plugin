package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.Optional;

public class BlockPointer {
    public final int x;
    public final int y;
    public final int z;

    public BlockPointer(Block block) {
        x = block.getX();
        y = block.getY();
        z = block.getZ();
    }

    public Optional<Block> getBlock(World world) {
        return Optional.ofNullable(world.getBlockAt(x, y, z));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPointer that = (BlockPointer) o;
        return x == that.x &&
                y == that.y &&
                z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
