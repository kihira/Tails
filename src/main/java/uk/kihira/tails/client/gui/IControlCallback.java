/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

public interface IControlCallback<T extends IControl<V>, V> {

    boolean onValueChange(T slider, V oldValue, V newValue);
}
