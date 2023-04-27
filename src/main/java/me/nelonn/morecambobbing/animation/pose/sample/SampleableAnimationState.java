package me.nelonn.morecambobbing.animation.pose.sample;

import me.nelonn.morecambobbing.animation.pose.AnimationPose;
import me.nelonn.morecambobbing.util.animation.LocatorSkeleton;
import me.nelonn.morecambobbing.util.data.AnimationDataContainer;

public class SampleableAnimationState {
    private final String identifier;

    public SampleableAnimationState(String identifier) {
        this.identifier = identifier;
    }

    public <L extends Enum<L>> AnimationPose<L> sample(LocatorSkeleton<L> locatorSkeleton, AnimationDataContainer.CachedPoseContainer cachedPoseContainer) {
        return AnimationPose.of(locatorSkeleton);
    }

    public <L extends Enum<L>> AnimationPose<L> sampleFromInputPose(AnimationPose<L> inputPose, LocatorSkeleton<L> locatorSkeleton, AnimationDataContainer.CachedPoseContainer cachedPoseContainer) {
        return this.sample(locatorSkeleton, cachedPoseContainer);
    }

    public void tick() {

    }

    public String getIdentifier() {
        return this.identifier;
    }

}
