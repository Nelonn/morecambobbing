package me.nelonn.morecambobbing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.nelonn.morecambobbing.MoreCamBobbingMain;
import me.nelonn.morecambobbing.animation.AnimatorDispatcher;
import me.nelonn.morecambobbing.animation.entity.FirstPersonPlayerAnimator;
import me.nelonn.morecambobbing.animation.entity.LivingEntityAnimator;
import me.nelonn.morecambobbing.animation.pose.AnimationPose;
import me.nelonn.morecambobbing.animation.pose.MutablePartPose;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    private boolean renderHand;

    @Shadow
    @Final
    private Camera mainCamera;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickEntityInformation(CallbackInfo ci) {
        if (this.minecraft.level != null) {
            for (Entity entity : this.minecraft.level.entitiesForRendering()) {
                if (entity instanceof LivingEntity) {
                    EntityType<?> entityType = entity.getType();
                    if (MoreCamBobbingMain.ENTITY_ANIMATORS.contains(entityType)) {
                        LivingEntityAnimator<?, ?, ?> livingEntityAnimator = MoreCamBobbingMain.ENTITY_ANIMATORS.get(entityType);
                        AnimatorDispatcher.INSTANCE.tickEntity((LivingEntity) entity, livingEntityAnimator);
                    }
                }
            }
            FirstPersonPlayerAnimator.INSTANCE.tickExternal();
        }
    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"),
                    to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lorg/joml/Matrix3f;)V")
            ))
    private void removeVanillaCameraRotation(PoseStack instance, Quaternionf quaternionf) {

    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lorg/joml/Matrix3f;)V"))
    private void injectCameraRotation(float f, long l, PoseStack poseStack, CallbackInfo ci) {
        if (this.minecraft.options.getCameraType().isFirstPerson() && this.renderHand) {
            if (FirstPersonPlayerAnimator.INSTANCE.localBakedPose != null) {
                AnimationPose<FirstPersonPlayerAnimator.FPPlayerLocators> animationPose = FirstPersonPlayerAnimator.INSTANCE.localBakedPose.getBlendedPose(f);
                MutablePartPose cameraPose = animationPose.getLocatorPose(FirstPersonPlayerAnimator.FPPlayerLocators.camera);

                PoseStack poseStack1 = new PoseStack();
                Vector3f cameraRot = cameraPose.getEulerRotation();
                cameraRot.z *= -1;
                cameraPose.setEulerRotation(cameraRot);

                poseStack1.mulPose(cameraPose.rotation);
                poseStack1.translate(cameraPose.x / 16F, cameraPose.y / 16F, cameraPose.z / -16F);
                Matrix4f matrix4f = poseStack1.last().pose();

                poseStack.mulPoseMatrix(matrix4f);

                poseStack.mulPose(Axis.XP.rotationDegrees(this.mainCamera.getXRot()));
                poseStack.mulPose(Axis.YP.rotationDegrees(this.mainCamera.getYRot() + 180.0f));
            }
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(this.mainCamera.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(this.mainCamera.getYRot() + 180.0f));
        }
    }
}
