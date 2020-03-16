package toma.configlib.config.sides;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import toma.configlib.ConfigLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public abstract class Manager {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public abstract void load();

    public static String readFileToString(File file) {
        StringBuilder builder = new StringBuilder();
        try(Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()),StandardCharsets.UTF_8)) {
            stream.forEach(string -> builder.append(string).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static void rewrite(JsonObject jsonObject, File file) {
        String jsonString = GSON.toJson(jsonObject);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            ConfigLib.log.fatal("Exception ocurred while write config files: {}", e.toString());
        }
    }
}
