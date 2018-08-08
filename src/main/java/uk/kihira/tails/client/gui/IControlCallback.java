package uk.kihira.tails.client.gui;

public interface IControlCallback<T extends IControl<V>, V> {

    boolean onValueChange(T slider, V oldValue, V newValue);
}
