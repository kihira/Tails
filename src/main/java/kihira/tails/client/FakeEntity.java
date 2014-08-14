package kihira.tails.client;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class FakeEntity extends EntityLiving {

    public FakeEntity(World world) {
        super(world);
    }

    @Override
    public void writeToNBT(NBTTagCompound p_70109_1_) {}

    @Override
    public void readFromNBT(NBTTagCompound p_70037_1_) {}
}
