package me.nelonn.morecambobbing.util.animation;

import com.google.common.collect.Maps;
import net.minecraft.client.model.geom.PartPose;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

public class LocatorSkeleton<L extends Enum<L>> {

    private final HashMap<Enum<L>, LocatorEntry<L>> locatorHashMap = Maps.newHashMap();

    public LocatorSkeleton() {

    }

    public static <L extends Enum<L>> LocatorSkeleton<L> of(Enum<L>[] locators) {
        return new LocatorSkeleton<L>().addLocators(locators);
    }

    private LocatorSkeleton<L> addLocators(Enum<L>[] locators) {
        for (Enum<L> locator : locators) {
            this.locatorHashMap.put(locator, new LocatorEntry<L>(locator));
        }
        return this;
    }

    public Set<Enum<L>> getLocators() {
        return locatorHashMap.keySet();
    }

    public PartPose getLocatorDefaultPose(Enum<L> locator) {
        return locatorHashMap.get(locator).defaultPose;
    }

    public LocatorSkeleton<L> setLocatorMirror(Enum<L> locator, Enum<L> mirrored) {
        this.locatorHashMap.get(locator).setMirroedLocatorIdentifier(mirrored);
        return this;
    }

    public String getLocatorModelPartIdentifier(Enum<L> locator) {
        return this.locatorHashMap.get(locator).getModelPartIdentifier();
    }

    public boolean getLocatorUsesModelPart(Enum<L> locator) {
        return this.locatorHashMap.get(locator).getUsesModelPart();
    }

    public LocatorSkeleton<L> setLocatorDefaultPose(Enum<L> locator, PartPose pose) {
        this.locatorHashMap.get(locator).setDefaultPose(pose);
        return this;
    }

    /*

    public void addLocatorModelPart(String locator, String locatorMirrored, String modelPartIdentifier, PartPose defaultPose){
        locatorHashMap.putIfAbsent(locator, new LocatorEntry(locator, locatorMirrored, modelPartIdentifier, defaultPose));
    }

    public void addLocatorModelPart(String locator, String modelPartIdentifier, PartPose defaultPose){
        addLocatorModelPart(locator, locator, modelPartIdentifier, defaultPose);
    }

    public void addLocatorModelPart(String locator, String locatorMirrored, String modelPartIdentifier){
        addLocatorModelPart(locator, locatorMirrored, modelPartIdentifier, PartPose.ZERO);
    }

    public void addLocatorModelPart(String locator, String modelPartIdentifier){
        addLocatorModelPart(locator, modelPartIdentifier, PartPose.ZERO);
    }

    public void addLocator(String locator, String locatorMirrored){
        locatorHashMap.putIfAbsent(locator, new LocatorEntry(locator, locatorMirrored, null, PartPose.ZERO));
    }

    public void addLocator(String locator){
        addLocator(locator, locator);
    }

     */

    public Enum<L> getMirroredLocator(Enum<L> locator) {
        return this.locatorHashMap.get(locator).getMirroedLocatorIdentifier();
    }

    /*
    @Nullable
    private getLocatorEntry(String identifier){
        return this.locatorHashMap.get(identifier);
    }

     */

    /*
    public boolean containsLocator(String identifier){
        return this.locatorHashMap.containsKey(identifier);
    }

     */

    public static class LocatorEntry<L extends Enum<L>> {
        //private final String locatorIdentifier;
        private Enum<L> mirroedLocatorIdentifier;
        @Nullable
        private String modelPartIdentifier;
        private boolean usesModelPart;
        private PartPose defaultPose;

        public LocatorEntry(Enum<L> mirroedLocator, @Nullable String modelPartIdentifier, PartPose defaultPose) {
            //this.locatorIdentifier = locatorIdentifier;
            this.mirroedLocatorIdentifier = mirroedLocator;
            this.modelPartIdentifier = modelPartIdentifier;
            this.usesModelPart = modelPartIdentifier != null;
            this.defaultPose = defaultPose;
        }

        public LocatorEntry(Enum<L> mirroredLocator) {
            this(mirroredLocator, null, PartPose.ZERO);
        }

        public Enum<L> getMirroedLocatorIdentifier() {
            return this.mirroedLocatorIdentifier;
        }

        public void setMirroedLocatorIdentifier(Enum<L> locator) {
            this.mirroedLocatorIdentifier = locator;
        }

        public void setDefaultPose(PartPose pose) {
            this.defaultPose = pose;
        }

        public String getModelPartIdentifier() {
            return this.usesModelPart ? this.modelPartIdentifier : "null";
        }

        public boolean getUsesModelPart() {
            return this.usesModelPart;
        }

        public PartPose getDefaultPose() {
            return this.defaultPose;
        }
    }
}
