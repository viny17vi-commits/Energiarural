package com.energiarural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class EnergyUtil {
    private EnergyUtil() {
    }

    /**
     * Empurra energia para os vizinhos (cabos, baterias, máquinas deste mod ou do Mekanism).
     * A energia só flui de quem está mais cheio (em %) para quem está mais vazio, então não volta para trás.
     */
    public static void push(Level level, BlockPos pos, EnergyBuffer buf, int maxRate) {
        for (Direction d : Direction.values()) {
            if (buf.energy <= 0) {
                return;
            }
            EnergyHandler target = level.getCapability(Capabilities.Energy.BLOCK, pos.relative(d), d.getOpposite());
            if (target == null) {
                continue;
            }
            double ct = target.getCapacityAsLong();
            if (ct <= 0) {
                continue;
            }
            double t = target.getAmountAsLong();
            double m = buf.energy;
            double cm = buf.capacity;
            long want = (long) ((m * ct - t * cm) / (cm + ct));
            if (want <= 0) {
                continue;
            }
            int amount = (int) Math.min(Math.min(want, maxRate), buf.energy);
            try (Transaction tx = Transaction.openRoot()) {
                int accepted = target.insert(amount, tx);
                if (accepted > 0) {
                    tx.commit();
                    buf.energy -= accepted;
                    buf.changed();
                }
            }
        }
    }
}
