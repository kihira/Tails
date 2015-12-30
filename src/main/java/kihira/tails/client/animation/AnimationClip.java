package kihira.tails.client.animation;

import net.minecraft.client.model.ModelRenderer;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class AnimationClip {

    public Map<ModelRenderer, ArrayBlockingQueue<AnimationSegment>> animSegmenents;

    public boolean loop;

    public AnimationClip(Map<ModelRenderer, ArrayBlockingQueue<AnimationSegment>> animSegmenents) {
        this.animSegmenents = animSegmenents;
    }

    /**
     * Updates this clip and causes each AnimationSegment to animate in order
     * @param time Current time
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
}
