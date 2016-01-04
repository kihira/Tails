package kihira.tails.client.animation;

import com.google.common.collect.Sets;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;

public class AnimationClip {

    public static final AnimationClip identityClip = new AnimationClip(null, true);

    private final Map<ModelRenderer, SortedSet<AnimationSegment>> animSegments;
    private final boolean loop;
    private int length = -1; // Cached for performance
    private int playTime = 0;

    public AnimationClip(Map<ModelRenderer, SortedSet<AnimationSegment>> animSegments, boolean loop) {
        this.animSegments = animSegments;
        this.loop = loop;
    }

    /**
     * Animates this clip and causes each AnimationSegment to animate if within their time frame
     * @param entity
     * @param partialTicks Partial time between ticks
     */
    public void animate(EntityLivingBase entity, float partialTicks) {
        for (Map.Entry<ModelRenderer, SortedSet<AnimationSegment>> entry : animSegments.entrySet()) {
            for (AnimationSegment segment : entry.getValue()) {
                if (playTime >= segment.startFrame && playTime <= segment.startFrame + segment.length) {
                    segment.animate(entry.getKey(), partialTicks);
                }
            }
        }
    }

    /**
     * Updates playTime each tick. If loop is set to false and playTime is larger then length, animation is set to finish
     * @return If this clip has finished playing
     */
    public boolean update() {
        playTime++;

        for (Map.Entry<ModelRenderer, SortedSet<AnimationSegment>> entry : animSegments.entrySet()) {
            for (AnimationSegment segment : entry.getValue()) {
                if (playTime >= segment.startFrame && playTime <= segment.startFrame + segment.length) {
                    segment.update();
                }
            }
        }

        if (playTime > getLength()) {
            if (loop) {
                playTime = 0;
            }
            else {
                finish();
                return true;
            }
        }
        return false;
    }

    public void addSegments(ModelRenderer renderer, AnimationSegment ... segments) {
        if (animSegments.containsKey(renderer)) {
            Collections.addAll(animSegments.get(renderer), segments);
        }
        else {
            SortedSet<AnimationSegment> set = Sets.newTreeSet();
            Collections.addAll(set, segments);
            animSegments.put(renderer, set);
        }
    }

    public void removeSegment(ModelRenderer renderer, AnimationSegment segment) {
        if (animSegments.containsKey(renderer)) {
            animSegments.get(renderer).remove(segment);
        }
    }

    public void removeAllSegments(ModelRenderer renderer) {
        animSegments.remove(renderer);
    }

    /**
     * Calculate total time length of the animation in ticks
     */
    private void calcLength() {
        int length = 0;
        for (Map.Entry<ModelRenderer, SortedSet<AnimationSegment>> entry : animSegments.entrySet()) {
            int setLength = 0;
            for (AnimationSegment segment : entry.getValue()) {
                if (segment.startFrame > setLength) {
                    setLength = segment.startFrame;
                }
                setLength += segment.length;
            }
            if (setLength > length) {
                length = setLength;
            }
        }
        this.length = length;
    }

    /**
     * Returns total time length of the animation in ticks
     * Total length is calculated via getLength and cached in length for performance
     * @return Total animation time length
     */
    public int getLength() {
        if (length == -1) {
            calcLength();
        }
        return length;
    }

    public void finish() {
        playTime = 0;
    }
}
