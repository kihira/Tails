package kihira.tails.client.animation;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationStateMachine {

    private final Map<String, AnimationState> animClips;
    private final Map<AnimationState, List<Transition>> transitions;
    private final Map<String, Parameter> parameters = new HashMap<>();
    private final EntityLivingBase entity;

    private AnimationState currState;
    private Transition currTrans;

    public AnimationStateMachine(EntityLivingBase entity, Map<String, AnimationState> animClips, Map<AnimationState, List<Transition>> transitions, String startClip) {
        this.entity = entity;
        this.animClips = animClips;
        this.transitions = transitions;

        transition(startClip);
    }

    public void transition(String newClip) {
        if (!animClips.containsKey(newClip)) {
            throw new IllegalArgumentException(String.format("The clip \"%s\" does not exist", newClip));
        }
        AnimationState clip = animClips.get(newClip);

        // TODO implement transitions
        //currState.getClip().finish();
        currState = clip;
    }

    public void registerState(String name, AnimationState clip) {
        if (animClips.containsKey(name)) {
            throw new IllegalArgumentException(String.format("A clip with the name \"%s\" is already registered", name));
        }
        animClips.put(name, clip);
    }

    public void update() {
        // Do transitions first if possible
        if (currTrans != null) {

        }
        if (currState.getClip().update()) {
            if (transitions.containsKey(currState)) {
                for (Transition transition : transitions.get(currState)) {
                    if (transition.shouldTransition(null, parameters)) {
                        currTrans = transition;
                    }
                }
            }
            // TODO transition
        }
    }

    public void animate(float partialRenderTick) {
        currState.getClip().animate(entity, partialRenderTick);
    }

    protected static float[] calculateMotionAngles(AbstractClientPlayer player) {
//        double xMotion = (player.chasingPosX - player.prevChasingPosX);
//        double yMotion = (player.chasingPosY - player.prevChasingPosY); //Positive when falling, negative when climbing
//        double zMotion = (player.chasingPosZ - player.prevChasingPosZ);
        double xMotion = (player.prevPosX - player.posX);
        double yMotion = (player.prevPosY - player.posY);
        double zMotion = (player.prevPosZ - player.posZ);
        float bodyYaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset); // Actual direction body is pointing
        double bodyYawSin = Math.sin(bodyYaw * Math.PI / 180F);
        double bodyYawCos = -Math.cos(bodyYaw * Math.PI / 180F);
        float xAngle = (float) ((-xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F);
        float yAngle = (float) (yMotion * 100f);
        float zAngle = (float) ((-xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F);

        return new float[] {xAngle, yAngle, zAngle};
    }
}
