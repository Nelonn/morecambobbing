package me.nelonn.morecambobbing.animation.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.nelonn.morecambobbing.access.ModelAccess;
import me.nelonn.morecambobbing.animation.AnimatorDispatcher;
import me.nelonn.morecambobbing.animation.pose.AnimationPose;
import me.nelonn.morecambobbing.animation.pose.BakedAnimationPose;
import me.nelonn.morecambobbing.animation.pose.sample.SampleableAnimationState;
import me.nelonn.morecambobbing.util.animation.LocatorSkeleton;
import me.nelonn.morecambobbing.util.data.AnimationDataContainer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;

import java.util.Random;

public abstract class LivingEntityAnimator<T extends LivingEntity, M extends EntityModel<T>, L extends Enum<L>> {
    protected T livingEntity;
    protected M entityModel;
    protected final LocatorSkeleton<L> locatorSkeleton;

    protected AnimationDataContainer entityAnimationData;
    protected final Random random = new Random();

    public LivingEntityAnimator() {
        this.locatorSkeleton = buildRig();
    }

    public void setEntity(T livingEntity) {
        this.livingEntity = livingEntity;
    }

    public void setEntityModel(M entityModel) {
        this.entityModel = entityModel;
    }

    protected LocatorSkeleton<L> buildRig() {
        return new LocatorSkeleton<L>();
    }

    public void tick(LivingEntity livingEntity, AnimationDataContainer entityAnimationData) {

    }

    protected AnimationPose<L> calculatePose() {
        return null;
    }

    protected void finalizeModelParts(ModelPart rootModelPart) {
    }

    protected AnimationDataContainer getEntityAnimationData() {
        return this.entityAnimationData;
    }

    protected <D> AnimationDataContainer.Variable<D> getEntityAnimationVariableObject(AnimationDataContainer.DataKey<D> dataKey) {
        return getEntityAnimationData().get(dataKey);
    }

    protected <D> D getEntityAnimationVariable(AnimationDataContainer.DataKey<D> dataKey) {
        return getEntityAnimationVariableObject(dataKey).get();
    }

    protected <D> void setEntityAnimationVariable(AnimationDataContainer.DataKey<D> dataKey, D value) {
        getEntityAnimationData().setValue(dataKey, value);
    }

    protected AnimationPose<L> sampleAnimationState(SampleableAnimationState sampleableAnimationState) {
        return getEntityAnimationData().sampleAnimationState(this.locatorSkeleton, sampleableAnimationState);
    }

    protected AnimationPose<L> sampleAnimationStateFromInputPose(SampleableAnimationState sampleableAnimationState, AnimationPose inputPose) {
        return getEntityAnimationData().sampleAnimationStateFromInputPose(inputPose.getCopy(), this.locatorSkeleton, sampleableAnimationState);
    }

    protected <D extends SampleableAnimationState> D getAnimationState(D sampleableAnimationState) {
        return getEntityAnimationData().getAnimationState(sampleableAnimationState);
    }

    protected WalkAnimationState getWalkAnimationState() {
        return this.livingEntity != null ? this.livingEntity.walkAnimation : new WalkAnimationState();
    }

    @SuppressWarnings("unchecked")
    public void tick(LivingEntity livingEntity) {
        BakedAnimationPose<L> bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUUID());
        AnimationDataContainer entityAnimationData = AnimatorDispatcher.INSTANCE.getEntityAnimationData(livingEntity.getUUID());
        this.entityAnimationData = entityAnimationData;
        this.setEntity((T) livingEntity);

        this.tick(livingEntity, entityAnimationData);
        getEntityAnimationData().tickAnimationStates();

        if (bakedPose == null) {
            bakedPose = new BakedAnimationPose<L>();
        }
        if (!bakedPose.hasPose) {
            bakedPose.setPose(AnimationPose.of(this.locatorSkeleton));
            bakedPose.hasPose = true;
        }
        bakedPose.pushToOld();

        AnimationPose<L> animationPose = this.calculatePose();
        if (animationPose == null) {
            animationPose = AnimationPose.of(this.locatorSkeleton);
        }
        animationPose.applyDefaultPoseOffset();

        bakedPose.setPose(animationPose.getCopy());
        AnimatorDispatcher.INSTANCE.saveBakedPose(livingEntity.getUUID(), bakedPose);
    }

    public void applyBakedPose(T livingEntity, M entityModel, PoseStack poseStack, AnimationDataContainer entityAnimationData, float partialTicks) {
        setEntity(livingEntity);
        setEntityModel(entityModel);

        BakedAnimationPose<L> bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUUID());

        ModelPart rootModelPart = getRoot(entityModel);
        assert bakedPose != null;
        bakedPose.bakeToModelParts(rootModelPart, partialTicks);
        finalizeModelParts(rootModelPart);
    }

    protected ModelPart getRoot(M entityModel) {
        return ((ModelAccess) entityModel).getRootModelPart();
    }
}
