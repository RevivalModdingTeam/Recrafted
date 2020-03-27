package dev.revivalmoddingteam.recrafted.util.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.revivalmoddingteam.recrafted.Recrafted;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class JsonHelper {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void createItemFiles(String itemModelsPath) {
        Collection<Item> collection = ForgeRegistries.ITEMS.getValues().stream().filter(i -> i.getRegistryName().getNamespace().equals(Recrafted.MODID)).collect(Collectors.toList());
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> collection.forEach(it -> createGeneratedItemFile(it, itemModelsPath)));
    }

    @OnlyIn(Dist.CLIENT)
    public static void createGeneratedItemFile(Item item, String assetsPath) {
        ResourceLocation registryName = item.getRegistryName();
        boolean flag = item instanceof BlockItem;
        String mod = registryName.getNamespace();
        String fileName = registryName.getPath();
        File file = new File(assetsPath, fileName + ".json");
        JsonObject object = new JsonObject();
        if(flag) {
            object.addProperty("parent", mod + ":block/" + fileName);
        } else {
            object.addProperty("parent", "item/generated");
            JsonObject texture = new JsonObject();
            texture.addProperty("layer0", mod + ":items/" + fileName);
            object.add("textures", texture);
        }
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(object));
            writer.close();
            Recrafted.log.info("Generated item file for item {}", registryName);
        } catch (IOException e) {
            Recrafted.log.fatal("Exception occurred while generating item file: " + e.toString());
        }
    }
}
