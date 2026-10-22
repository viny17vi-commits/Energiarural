package com.energiarural;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/** Bateria interna. Usa o mesmo sistema de energia (EnergyHandler/FE) que o Mekanism usa. */
public class EnergyBuffer extends SnapshotJournal<Integer> implements EnergyHandler {
    public int energy;
    public final int capacity;
    private final int maxIn;
    private final int maxOut;
    private final Runnable onChange;

    public EnergyBuffer(int capacity, int maxIn, int maxOut, Runnable onChange) {
        this.capacity = capacity;
        this.maxIn = maxIn;
        this.maxOut = maxOut;
        this.onChange = onChange;
    }

    public void changed() {
        onChange.run();
    }

    /** Gera energia direto no buffer (usado por geradores). */
    public void generate(int amount) {
        energy = Math.min(capacity, energy + amount);
    }

    @Override
    public long getAmountAsLong() {
        return energy;
    }

    @Override
    public long getCapacityAsLong() {
        return capacity;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        int accepted = Math.min(Math.min(amount, maxIn), capacity - energy);
        if (accepted <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        energy += accepted;
        return accepted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        int taken = Math.min(Math.min(amount, maxOut), energy);
        if (taken <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        energy -= taken;
        return taken;
    }

    @Override
    protected Integer createSnapshot() {
        return energy;
    }

    @Override
    protected void revertToSnapshot(Integer snapshot) {
        energy = snapshot;
    }

    @Override
    protected void onRootCommit(Integer originalState) {
        super.onRootCommit(originalState);
        if (originalState != energy) {
            onChange.run();
        }
    }
}
