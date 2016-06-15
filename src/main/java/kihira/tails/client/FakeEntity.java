/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class FakeEntity extends EntityLiving {

    public FakeEntity(World world) {
        super(world);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {}
}
