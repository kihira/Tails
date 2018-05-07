package uk.kihira.gltf;

import javax.annotation.Nullable;

/**
 * Image data used to create a texture. Image can be referenced by URI or `bufferView` index. `mimeType` is required in the latter case.
 */
class Image {
    /**
     * The uri of the image.  
     * 
     * Relative paths are relative to the .gltf file.
     * Instead of referencing an external file, the uri can also be a data-uri.
     * The image format must be jpg or png.
     */
    @Nullable
    public String uri;

    /**
     * The image's MIME type.
     * This is not null if {@link #bufferView} is not null
     * 
     * Values: "image/jpeg", "image/png"
     */
    @Nullable
    public String mimeType;

    /**
     * The index of the bufferView that contains the image.
     * Use this instead of the image's uri property.
     */
    @Nullable
    public Integer bufferView;
}