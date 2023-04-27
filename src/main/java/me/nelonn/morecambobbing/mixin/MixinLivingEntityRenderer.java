package me.nelonn.morecambobbing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.nelonn.morecambobbing.animation.AnimatorDispatcher;
import me.nelonn.morecambobbing.animation.pose.BakedAnimationPose;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private final String ROOT = "root";

    @Shadow
    protected M model;

    @Shadow
    public abstract M getModel();

    @Shadow
    protected abstract void setupRotations(T livingEntity, PoseStack poseStack, float f, float g, float h);

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
    private void redirectSetupAnim(M entityModel, Entity t, float a, float b, float c, float d, float e, T livingEntity, float f, float g, PoseStack poseStack) {
        if (!AnimatorDispatcher.INSTANCE.animateEntity(livingEntity, entityModel, poseStack, g)) {
            entityModel.setupAnim(livingEntity, a, b, c, d, e);
        }
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V"))
    private void overwriteSetupRotations(LivingEntityRenderer<T, M> instance, T livingEntity, PoseStack poseStack, float bob, float bodyRot, float frameTime) {

        //poseStack.translate(Mth.sin(bob / 6), 0, 0);
        //poseStack.mulPose(Vector3f.ZP.rotation(Mth.sin(bob / 6) / 4));

        BakedAnimationPose<?> bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUUID());

        if (shouldUseAlternateRotations(bakedPose)) {

            poseStack.popPose();
            poseStack.pushPose();

            if (livingEntity.getPose() == Pose.SLEEPING) {
                Direction i = ((LivingEntity) livingEntity).getBedOrientation();
                float j = i != null ? sleepDirectionToRotation(i) : bodyRot;
                poseStack.mulPose(Axis.YP.rotationDegrees(j - 90));
            } else {
                bodyRot = 0;//Mth.rotLerp(frameTime, ((LivingEntity)livingEntity).yHeadRotO, ((LivingEntity)livingEntity).yHeadRot);
                //bodyRot = AnimatorDispatcher.INSTANCE.getEntityAnimationData(livingEntity).getLerped(LivingEntityPartAnimator.BODY_Y_ROT, frameTime);
                if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof AbstractMinecart) {
                    bodyRot = Mth.rotLerp(frameTime, ((LivingEntity) livingEntity).yHeadRotO, ((LivingEntity) livingEntity).yHeadRot);
                }

                poseStack.mulPose(Axis.YP.rotationDegrees(180 - bodyRot));
            }

        } else {
            this.setupRotations(livingEntity, poseStack, bob, bodyRot, frameTime);
        }
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0))
    private void removeBedTranslation(PoseStack instance, float d, float e, float f, T livingEntity) {
        BakedAnimationPose<?> bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUUID());
        if (shouldUseAlternateRotations(bakedPose)) {

        } else {
            instance.translate(d, e, f);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void translateAndRotateAfterScale(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {

    }

    private boolean shouldUseAlternateRotations(BakedAnimationPose<?> bakedPose) {
        return false;
    }

    private static float sleepDirectionToRotation(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }
}
