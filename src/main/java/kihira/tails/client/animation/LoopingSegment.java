package kihira.tails.client.animation;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import net.minecraft.client.model.ModelRenderer;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;

public class LoopingSegment extends AnimationSegment {
    private final ArrayDeque<AnimationSegment> segments;
    private AnimationSegment currSegment;
    private int offset = 0;

    private LoopingSegment(Variable variable, int length, SortedSet<AnimationSegment> segments) {
        super(variable, 0, length, segments.first().interpolation);
        this.segments = Queues.newArrayDeque();

        // Support for gaps between segments by adding a blank one (ignores gaps at before first segment and after last)
        Iterator<AnimationSegment> iterator = segments.iterator();
        AnimationSegment prevSegment = iterator.next();
        this.segments.addLast(prevSegment);
        while (iterator.hasNext()) {
            AnimationSegment currSegment = iterator.next();
            int prevSegmentEnd =  prevSegment.startFrame + prevSegment.length;
            if (currSegment.startFrame > prevSegmentEnd) {
                this.segments.addLast(new Blank(prevSegmentEnd, currSegment.startFrame - prevSegmentEnd));
            }
            this.segments.addLast(currSegment);
        }

        advanceSegment();
        // Calculate first part if required
        if (currSegment.startFrame > 0) {
            while (true) {
                advanceSegment();
                if (currSegment.startFrame > offset) {
                    offset = startFrame;
                }
                offset += currSegment.length;
                // Found wrap around
                if (offset > length) {
                    currSegment.playTime = offset - length;
                    offset = currSegment.startFrame;
                    playTime = offset + currSegment.playTime;
                    break;
                }
            }
        }
    }

    @Override
    public void animate(ModelRenderer model, float partialTicks) {
        currSegment.animate(model, partialTicks);
    }

    @Override
    public boolean update() {
        super.update();
        if (currSegment.update()) {
            advanceSegment();
        }
        return false;
    }

    private void advanceSegment() {
        currSegment = this.segments.pollFirst();
        this.segments.addLast(currSegment);
    }

    public static LoopingSegment createLoopingSegment(int totalLength, AnimationSegment ... segments) {
        int calculatedLength = 0;
        Variable var = segments[0].variable;

        for (AnimationSegment segment : segments) {
            if (segment.variable != var) {
                throw new IllegalArgumentException("All animation segments must be of the same variable!");
            }
            if (segment.startFrame > calculatedLength) {
                calculatedLength = segment.startFrame;
            }
            calculatedLength += segment.length;
        }
        if (totalLength < calculatedLength) {
            totalLength = calculatedLength;
        }

        SortedSet<AnimationSegment> set = Sets.newTreeSet();
        Collections.addAll(set, segments);
        return new LoopingSegment(var, totalLength, set);
    }
}
