package kihira.tails.client.animation;

import net.minecraft.client.model.ModelRenderer;

import java.util.ArrayDeque;
import java.util.Map;

public class AnimationClip {

    private final Map<ModelRenderer, ArrayDeque<AnimationSegment>> animSegmenents;

    private final boolean loop;
    private int length = -1; // Cached for performance

    public AnimationClip(Map<ModelRenderer, ArrayDeque<AnimationSegment>> animSegmenents, boolean loop) {
        this.animSegmenents = animSegmenents;
        this.loop = loop;
        calcLength();
    }

    /**
     * Updates this clip and causes each AnimationSegment to animate in order
     * @param worldTime Current world time
     * @param partialTicks Partial tick time between render
     * @return If the clip is complete
     */
    // TODO track play time ourselves (instead of via worldTime)
    public boolean update(float worldTime, float partialTicks) {
        for (Map.Entry<ModelRenderer, ArrayDeque<AnimationSegment>> entry : animSegmenents.entrySet()) {
            ArrayDeque<AnimationSegment> queue = entry.getValue();
            AnimationSegment segment = queue.peekFirst();
            float time = worldTime % getLength();

            // Move component to back of queue if completed and looping
            if (time < segment.startFrame || time >= segment.startFrame + segment.length) {
                if (loop) {
                    queue.addLast(segment);
                }
                queue.removeFirst();
                segment = queue.peekFirst();
                if (segment == null) {
                    return false;
                }
            }
            segment.animate(entry.getKey(), time + partialTicks);
        }
        return true;
    }

    private void calcLength() {
        int length = 0;
        for (Map.Entry<ModelRenderer, ArrayDeque<AnimationSegment>> entry : animSegmenents.entrySet()) {
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
