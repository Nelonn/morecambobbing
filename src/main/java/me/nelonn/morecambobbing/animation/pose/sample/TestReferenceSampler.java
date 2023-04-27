package me.nelonn.morecambobbing.animation.pose.sample;

import me.nelonn.morecambobbing.animation.pose.AnimationPose;
import me.nelonn.morecambobbing.util.animation.LocatorSkeleton;
import me.nelonn.morecambobbing.util.data.AnimationDataContainer;

public class TestReferenceSampler extends SampleableAnimationState {
    private String cachedPoseIdentifier;

    private TestReferenceSampler(String identifier, String cachedPoseIdentifier) {
        super(identifier);
        this.cachedPoseIdentifier = cachedPoseIdentifier;
    }

    public static TestReferenceSampler of(String identifier, String cachedPoseIdentifier) {
        return new TestReferenceSampler(identifier, cachedPoseIdentifier);
    }

    @Override
    public AnimationPose sample(LocatorSkeleton locatorSkeleton, AnimationDataContainer.CachedPoseContainer cachedPoseContainer) {
        return cachedPoseContainer.getCachedPose(this.cachedPoseIdentifier, locatorSkeleton);
    }

    public void tick() {

    }
}
