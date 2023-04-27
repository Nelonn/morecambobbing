package me.nelonn.morecambobbing.animation;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import me.nelonn.morecambobbing.MoreCamBobbingMain;
import me.nelonn.morecambobbing.animation.entity.LivingEntityAnimator;
import me.nelonn.morecambobbing.animation.pose.BakedAnimationPose;
import me.nelonn.morecambobbing.util.data.AnimationDataContainer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class AnimatorDispatcher {
    public static final AnimatorDispatcher INSTANCE = new AnimatorDispatcher();

    private final HashMap<UUID, AnimationDataContainer> entityAnimationDataMap = Maps.newHashMap();
    private final HashMap<UUID, BakedAnimationPose<?>> bakedPoseMap = Maps.newHashMap();

    public AnimatorDispatcher() {
    }

    public void tickEntity(LivingEntity livingEntity, LivingEntityAnimator<?, ?, ?> livingEntityPartAnimator) {
        if (!entityAnimationDataMap.containsKey(livingEntity.getUUID())) {
            entityAnimationDataMap.put(livingEntity.getUUID(), new AnimationDataContainer());
        }
        livingEntityPartAnimator.tick(livingEntity);
    }

    public <T extends LivingEntity, M extends EntityModel<T>, L extends Enum<L>> boolean animateEntity(T livingEntity, M entityModel, PoseStack poseStack, float partialTicks) {
        if (entityAnimationDataMap.containsKey(livingEntity.getUUID())) {
            if (MoreCamBobbingMain.ENTITY_ANIMATORS.contains(livingEntity.getType())) {
                LivingEntityAnimator<T, M, L> livingEntityPartAnimator = (LivingEntityAnimator<T, M, L>) MoreCamBobbingMain.ENTITY_ANIMATORS.get(livingEntity.getType());
                livingEntityPartAnimator.applyBakedPose(livingEntity, entityModel, poseStack, entityAnimationDataMap.get(livingEntity.getUUID()), partialTicks);
                return true;
            }
        }
        return false;
    }

    public void saveBakedPose(UUID uuid, BakedAnimationPose bakedPose) {
        this.bakedPoseMap.put(uuid, bakedPose);
    }

    @Nullable
    public <L extends Enum<L>> BakedAnimationPose<L> getBakedPose(UUID uuid) {
        if (this.bakedPoseMap.containsKey(uuid)) {
            return (BakedAnimationPose<L>) this.bakedPoseMap.get(uuid);
        }
        return null;
    }

    public boolean hasAnimationData(UUID uuid) {
        return this.entityAnimationDataMap.containsKey(uuid);
    }

    public AnimationDataContainer getEntityAnimationData(UUID uuid) {
        if (entityAnimationDataMap.containsKey(uuid)) {
            return entityAnimationDataMap.get(uuid);
        }
        return new AnimationDataContainer();
    }

    public <T extends Entity> AnimationDataContainer getEntityAnimationData(T entity) {
        return getEntityAnimationData(entity.getUUID());
    }
}
