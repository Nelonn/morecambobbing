package me.nelonn.morecambobbing.animation.pose;

import net.minecraft.client.model.geom.ModelPart;

public class BakedAnimationPose<L extends Enum<L>> {

    private AnimationPose<L> pose;
    private AnimationPose<L> poseOld;
    public boolean hasPose;

    public BakedAnimationPose() {
        this.hasPose = false;
    }

    public void pushToOld() {
        this.poseOld = this.pose.getCopy();
    }

    public void setPose(AnimationPose<L> animationPose) {
        this.pose = animationPose;
    }

    public AnimationPose<L> getBlendedPose(float partialTicks) {
        // uncomment this for debugging
        //partialTicks = 0;
        return this.poseOld.getBlendedLinear(this.pose, partialTicks);
    }

    public void bakeToModelParts(ModelPart rootModelPart, float partialTicks) {
        AnimationPose<L> blendedPose = getBlendedPose(partialTicks);
        for (Enum<L> locator : this.pose.getSkeleton().getLocators()) {
            if (this.pose.getSkeleton().getLocatorUsesModelPart(locator)) {
                ModelPart finalModelPart = rootModelPart;
                for (String individualPartString : this.pose.getSkeleton().getLocatorModelPartIdentifier(locator).split("\\.")) {
                    finalModelPart = finalModelPart.getChild(individualPartString);
                }
                finalModelPart.loadPose(blendedPose.getLocatorPose(locator).asPartPose());
            }
        }
    }
}
