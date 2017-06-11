package uk.kihira.tails.client.gui;

public interface IControl<V> {

    void setValue(V newValue);

    V getValue();
}
