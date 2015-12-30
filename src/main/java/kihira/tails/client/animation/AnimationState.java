package kihira.tails.client.animation;

public class AnimationState {

    public static final AnimationState ANY = new AnimationState(null);

    private final AnimationClip clip;

    public AnimationState(AnimationClip clip) {
        this.clip = clip;
    }

    public AnimationClip getClip() {
        return clip;
    }
}
