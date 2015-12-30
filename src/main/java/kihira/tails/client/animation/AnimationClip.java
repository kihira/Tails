package kihira.tails.client.animation;

import net.minecraft.client.model.ModelRenderer;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class AnimationClip {

    private final Map<ModelRenderer, ArrayBlockingQueue<AnimationSegment>> animSegmenents;

    private final boolean loop;
    private int length = -1; // Cached for performance

    public AnimationClip(Map<ModelRenderer, ArrayBlockingQueue<AnimationSegment>> animSegmenents, boolean loop) {
        this.animSegmenents = animSegmenents;
        this.loop = loop;
        calcLength();
    }

    /**
     * Updates this clip and causes each AnimationSegment to animate in order
     * @param time Current world time
     * @return If the clip is complete
     */
    public boolean update(float time) {
        for (Map.Entry<ModelRenderer, ArrayBlockingQueue<AnimationSegment>> entry : animSegmenents.entrySet()) {
            AnimationSegment segment = entry.getValue().peek();
            if (segment == null) {
                return false;
            }
            segment.animate(entry.getKey(), time);
            // Move component to back of queue if completed and looping
            if (time >= segment.startFrame + segment.length) {
                entry.getValue().remove();
                if (loop) {
                    entry.getValue().add(segment);
                }
            }
        }
        return true;
    }

    private void calcLength() {
        int length = 0;
        for (Map.Entry<ModelRenderer, ArrayBlockingQueue<AnimationSegment>> entry : animSegmenents.entrySet()) {
            for (AnimationSegment segment : entry.getValue()) {
                length += segment.length;
            }
        }
        this.length = length;
    }

    public int getLength() {
        if (length == -1) {
            calcLength();
        }
        return length;
    }
}
