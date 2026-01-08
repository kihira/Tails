package uk.kihira.tails.client.gui;

public interface IControl<V>
{
    boolean setValue(V newValue);

    V getValue();
}
