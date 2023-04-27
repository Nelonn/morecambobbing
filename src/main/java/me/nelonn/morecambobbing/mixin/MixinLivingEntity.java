package me.nelonn.morecambobbing.mixin;

import me.nelonn.morecambobbing.access.LivingEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Unique
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements LivingEntityAccess {

    @Shadow
    public abstract void lerpHeadTo(float f, int i);

    private String songName;
    private boolean songPlaying;
    private BlockPos songOrigin = new BlockPos(0, 0, 0);

    private String equippedArmor = "";

    public boolean useInventoryRenderer = false;

    /*
    public float getAnimationVariable(String variableType){
        return 4F;
    }
    public void setAnimationVariable(String variableType, float newValue){
    }

     */

    public boolean getUseInventoryRenderer() {
        return useInventoryRenderer;
    }

    public void setUseInventoryRenderer(boolean bool) {
        useInventoryRenderer = bool;
    }
}
