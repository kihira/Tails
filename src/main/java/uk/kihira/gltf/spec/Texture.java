package uk.kihira.gltf.spec;

import javax.annotation.Nullable;

public class Texture {
    /**
     * The index of the sampler used by this texture.
     * When undefined, a sampler with repeat wrapping and auto filtering should be used.
     */
    @Nullable
    public Integer sampler;

    /**
     * The index of the image used by this texture.
     */
    @Nullable
    public Integer source;

    /**
     * The user-defined name of this object.
     * This is not necessarily unique, e.g., an accessor and a buffer could have the same name, or two accessors could even have the same name.
     */
    @Nullable
    public String name;
}
