package me.nelonn.morecambobbing.util.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.nelonn.morecambobbing.MoreCamBobbingMain;
import me.nelonn.morecambobbing.util.time.ChannelTimeline;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TimelineGroupDataLoader implements SimpleResourceReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final String FORMAT_VERSION = "0.2";

    @Override
    public CompletableFuture<Map<ResourceLocation, JsonElement>> load(ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
        Gson gson = new Gson();

        Map<ResourceLocation, Resource> passedFiles = resourceManager.listResources("timelinegroups", string -> string.toString().endsWith(".json"));

        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
            for (ResourceLocation resourceLocation : passedFiles.keySet()) {
                String resourceLocationPath = resourceLocation.getPath();
                try {
                    Optional<Resource> resourceOptional = resourceManager.getResource(resourceLocation);
                    Resource resource = resourceOptional.orElse(null);
                    try {
                        InputStream inputStream = resource.open();
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                            try {
                                JsonElement jsonElement = GsonHelper.fromJson(gson, reader, JsonElement.class);
                                if (jsonElement != null) {
                                    map.put(resourceLocation, jsonElement);
                                } else {
                                    MoreCamBobbingMain.LOGGER.error("Couldn't load data file {} as it's null or empty", resourceLocation);
                                }
                            } catch (Throwable bufferedReaderThrowable) {
                                try {
                                    reader.close();
                                } catch (Throwable var16) {
                                    bufferedReaderThrowable.addSuppressed(var16);
                                }
                                throw bufferedReaderThrowable;
                            }
                            reader.close();
                        } catch (Throwable inputStreamThrowable) {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable closeInputStreamThrowable) {
                                    inputStreamThrowable.addSuppressed(closeInputStreamThrowable);
                                }
                            }
                            throw inputStreamThrowable;
                        }
                        inputStream.close();
                    } catch (Throwable resourceThrowable) {
                        if (resource != null) {
                            try {
                                //resource.close();
                            } catch (Throwable closeResourceThrowable) {
                                resourceThrowable.addSuppressed(closeResourceThrowable);
                            }
                        }
                        throw resourceThrowable;
                    }
                    //resource.close();
                } catch (IOException e) {
                    MoreCamBobbingMain.LOGGER.error("Error parsing data upon grabbing resource for resourceLocation " + resourceLocation);
                }
            }
            return map;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Map<ResourceLocation, JsonElement> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            TimelineGroupData newData = new TimelineGroupData();
            for (ResourceLocation resourceLocationKey : data.keySet()) {
                JsonElement animationJSON = data.get(resourceLocationKey);


                String resourceNamespace = resourceLocationKey.toString().split(":")[0];
                String resourceBody = resourceLocationKey.toString().split(":")[1].split("\\.")[0].replace("timelinegroups/", "");
                ResourceLocation finalResourceLocation = new ResourceLocation(resourceNamespace, resourceBody);

                float frameTime = animationJSON.getAsJsonObject().get("frame_length").getAsFloat() / 1.2F;
                String formatVersion;
                if (animationJSON.getAsJsonObject().has("format_version")) {
                    formatVersion = animationJSON.getAsJsonObject().get("format_version").getAsString();
                } else {
                    formatVersion = "0.1";
                }

                if (Objects.equals(formatVersion, FORMAT_VERSION)) {
                    TimelineGroupData.TimelineGroup timelineGroup = new TimelineGroupData.TimelineGroup(frameTime);

                    JsonArray partArrayJSON = animationJSON.getAsJsonObject().get("parts").getAsJsonArray();
                    for (int partIndex = 0; partIndex < partArrayJSON.size(); partIndex++) {
                        JsonObject partJSON = partArrayJSON.get(partIndex).getAsJsonObject();
                        String partName = partJSON.get("name").getAsString();
                        //AnimationOverhaul.LOGGER.info(partName);

                        ChannelTimeline channelTimeline = new ChannelTimeline();

                        JsonObject partKeyframesJSON = partJSON.get("keyframes").getAsJsonObject();
                        for (Map.Entry<String, JsonElement> keyframeEntry : partKeyframesJSON.entrySet()) {
                            int keyframeNumber = Integer.parseInt(keyframeEntry.getKey());
                            JsonElement keyframeJSON = keyframeEntry.getValue();
                            //AnimationOverhaul.LOGGER.info(keyframeNumber);

                            channelTimeline.addKeyframe(TransformChannel.x, keyframeNumber, keyframeJSON.getAsJsonObject().get("translate").getAsJsonArray().get(0).getAsFloat());
                            channelTimeline.addKeyframe(TransformChannel.y, keyframeNumber, keyframeJSON.getAsJsonObject().get("translate").getAsJsonArray().get(1).getAsFloat());
                            channelTimeline.addKeyframe(TransformChannel.z, keyframeNumber, keyframeJSON.getAsJsonObject().get("translate").getAsJsonArray().get(2).getAsFloat());

                            channelTimeline.addKeyframe(TransformChannel.xRot, keyframeNumber, keyframeJSON.getAsJsonObject().get("rotate").getAsJsonArray().get(0).getAsFloat());
                            channelTimeline.addKeyframe(TransformChannel.yRot, keyframeNumber, keyframeJSON.getAsJsonObject().get("rotate").getAsJsonArray().get(1).getAsFloat());
                            channelTimeline.addKeyframe(TransformChannel.zRot, keyframeNumber, keyframeJSON.getAsJsonObject().get("rotate").getAsJsonArray().get(2).getAsFloat());
                        }
                        timelineGroup.addPartTimeline(partName, channelTimeline);
                    }


                    newData.put(finalResourceLocation, timelineGroup);
                    //AnimationOverhaul.LOGGER.info(frameTime);
                    //AnimationOverhaul.LOGGER.info("Entity key: {} Animation key: {}", entityKey, animationKey);

                    MoreCamBobbingMain.LOGGER.info("Successfully loaded animation {}", resourceLocationKey);
                } else {
                    MoreCamBobbingMain.LOGGER.error("Failed to load animation {} (Animation format version was {}, not up to date with {})", resourceLocationKey, formatVersion, FORMAT_VERSION);
                }
            }

            TimelineGroupData.INSTANCE.clearAndReplace(newData);
        });
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("timeline_group_loader");
    }
}
