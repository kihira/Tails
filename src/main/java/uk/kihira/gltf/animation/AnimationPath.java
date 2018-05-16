package uk.kihira.gltf.animation;

import com.google.gson.annotations.SerializedName;

public enum AnimationPath {
    @SerializedName("translation")
    TRANSLATION,
    @SerializedName("rotation")
    ROTATION,
    @SerializedName("scale")
    SCALE,
    @SerializedName("weights")
    WEIGHTS
}
