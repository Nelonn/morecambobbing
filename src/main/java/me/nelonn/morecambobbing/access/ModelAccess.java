package me.nelonn.morecambobbing.access;

import net.minecraft.client.model.geom.ModelPart;

public interface ModelAccess {
    ModelPart getModelPart(String identifier);

    ModelPart getRootModelPart();
}
