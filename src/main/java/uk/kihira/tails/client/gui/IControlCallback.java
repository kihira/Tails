package uk.kihira.tails.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IControlCallback<T extends IControl<V>, V> {

    boolean onValueChange(T slider, V oldValue, V newValue);
}
