package me.nelonn.morecambobbing.mixin;

import me.nelonn.morecambobbing.animation.entity.FirstPersonPlayerAnimator;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "startAttack", at = @At("HEAD"))
    public void injectOnStartAttack(CallbackInfoReturnable<Boolean> cir) {
        FirstPersonPlayerAnimator.INSTANCE.localAnimationDataContainer.setValue(FirstPersonPlayerAnimator.IS_ATTACKING, true);
    }

    @Inject(method = "startUseItem", at = @At("HEAD"))
    public void injectOnStartUseItem(CallbackInfo ci) {
        FirstPersonPlayerAnimator.INSTANCE.localAnimationDataContainer.setValue(FirstPersonPlayerAnimator.IS_USING_ITEM, true);
    }

    @Inject(method = "continueAttack", at = @At("HEAD"))
    public void injectOnContinueAttackIsNotMining(boolean bl, CallbackInfo ci) {
        FirstPersonPlayerAnimator.INSTANCE.localAnimationDataContainer.setValue(FirstPersonPlayerAnimator.IS_MINING, false);
    }

    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;crack(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V"))
    public void injectOnContinueAttackIsMining(boolean bl, CallbackInfo ci) {
        FirstPersonPlayerAnimator.INSTANCE.localAnimationDataContainer.setValue(FirstPersonPlayerAnimator.IS_MINING, true);
    }
}
