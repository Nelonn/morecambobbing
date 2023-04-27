package me.nelonn.morecambobbing.access;

import me.nelonn.morecambobbing.util.animation.LocatorSkeleton;
import net.minecraft.core.BlockPos;

public interface LivingEntityAccess {
    void setRecordPlayerNearbyValues(String songName, boolean songPlaying, BlockPos songOrigin);

    boolean getIsSongPlaying();

    BlockPos getSongOrigin();

    String getSongName();

    String getPreviousEquippedArmor();

    void setEquippedArmor(String currentArmor);

    boolean getUseInventoryRenderer();

    void setUseInventoryRenderer(boolean bool);

    LocatorSkeleton getLocatorRig();

    void storeLocatorRig(LocatorSkeleton locatorRig);
}
