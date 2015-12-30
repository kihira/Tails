package kihira.tails.client.animation;

import com.google.common.collect.Sets;
import net.minecraft.client.model.ModelRenderer;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;

public class AnimationClip {

    private final Map<ModelRenderer, SortedSet<AnimationSegment>> animSegmenents;

    private final boolean loop;
    private int length = -1; // Cached for performance
    private int playTime = 0;

    public AnimationClip(Map<ModelRenderer, SortedSet<AnimationSegment>> animSegmenents, boolean loop) {
        this.animSegmenents = animSegmenents;
        this.loop = loop;
        calcLength();
    }

    /**
     * Animates this clip and causes each AnimationSegment to animate if within their time frame
     * @param partialTicks Partial time between ticks
     */
    public void animate(float partialTicks) {
        for (Map.Entry<ModelRenderer, SortedSet<AnimationSegment>> entry : animSegmenents.entrySet()) {
            for (AnimationSegment segment : entry.getValue()) {
                if (playTime >= segment.startFrame && playTime <= segment.startFrame + segment.length) {
                    segment.animate(entry.getKey(), playTime + partialTicks);
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
        if (animSegmenents.containsKey(renderer)) {
            Collections.addAll(animSegmenents.get(renderer), segments);
        }
        else {
            SortedSet<AnimationSegment> set = Sets.newTreeSet();
            Collections.addAll(set, segments);
            animSegmenents.put(renderer, set);
        }
    }

    public void removeSegment(ModelRenderer renderer, AnimationSegment segment) {
        if (animSegmenents.containsKey(renderer)) {
            animSegmenents.get(renderer).remove(segment);
        }
    }

    public void removeAllSegments(ModelRenderer renderer) {
        animSegmenents.remove(renderer);
    }

    /**
     * Calculate total time length of the animation in ticks
     */
    private void calcLength() {
        int length = 0;
        for (Map.Entry<ModelRenderer, SortedSet<AnimationSegment>> entry : animSegmenents.entrySet()) {
            for (AnimationSegment segment : entry.getValue()) {
                length += segment.length;
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
