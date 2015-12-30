package kihira.tails.client.animation;

import kihira.tails.client.animation.interpolation.IInterpolation;

import java.util.HashMap;
import java.util.Map;

public class Transition {

    private final Map<String, Parameter> parameters = new HashMap<>();
    private final AnimationState startState;
    private final AnimationState endState;
    private final IInterpolation interpolation;
    private final int duration;
    public final boolean defaultTrans;

    private int playTime;

    public Transition(AnimationState startState, AnimationState endState, IInterpolation interpolation, int duration, boolean defaultTrans) {
        this.startState = startState;
        this.endState = endState;
        this.interpolation = interpolation;
        this.duration = duration;
        this.defaultTrans = defaultTrans;
    }

    public boolean shouldTransition(AnimationState newState, Map<String, Parameter> parameters) {
        // Check parameters are correct
        for (Map.Entry<String, Parameter> entry : this.parameters.entrySet()) {
            if (!parameters.containsKey(entry.getKey()) || parameters.get(entry.getKey()).checkValid(entry.getValue().getValue())) {
                return false;
            }
        }

        // Only transition if we're going to our endState or startState is ANY
        return newState == endState || startState == AnimationState.ANY;
    }

    public boolean update() {
        playTime++;
        if (playTime > duration) {
            playTime = 0;
            return true;
        }
        return false;
    }

    public void animate(float partialTicks) {

    }
}
