import com.google.gson.*;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        JFrame frame = new JFrame("JSON Util");
        frame.setResizable(false);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        JTextArea inputPath = new JTextArea("desktop-file-name.json");
        inputPath.setBounds(10, 10, 375, 20);
        inputPath.setVisible(true);
        frame.add(inputPath);
        JLabel info = new JLabel("");
        info.setBounds(10, 75, 375, 20);
        info.setVisible(true);
        frame.add(info);
        JButton modify = new JButton("Modify");
        modify.setBounds(10, 30, 375, 20);
        modify.setVisible(true);
        modify.addActionListener(a -> {
            try {
                info.setText("");
                info.repaint();
                File desktop = FileSystemView.getFileSystemView().getHomeDirectory();
                String path = inputPath.getText().contains(".json") ? inputPath.getText() : inputPath.getText() + ".json";
                File toModify = new File(desktop.getAbsolutePath(), path);
                if(!toModify.exists()) {
                    info.setText("Couldn't find file with name " + path + " in desktop!");
                    info.repaint();
                    return;
                }
                StringBuilder fileToStringBuilder = new StringBuilder();
                try(Stream<String> stream = Files.lines(Paths.get(toModify.getAbsolutePath()), StandardCharsets.UTF_8)) {
                    stream.forEach(string -> fileToStringBuilder.append(string).append("\n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String rawJson = fileToStringBuilder.toString();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(rawJson).getAsJsonObject();
                JsonArray elements = obj.getAsJsonArray("elements");
                String[] faces = {"down", "up", "north", "south", "west", "east"};
                for(JsonElement element : elements) {
                    JsonObject quad = element.getAsJsonObject();
                    JsonObject data = quad.getAsJsonObject("faces");
                    for(String face : faces) {
                        JsonObject faceData = data.getAsJsonObject(face);
                        if(faceData.get("texture").getAsString().contains("lea")) {
                            JsonPrimitive object = new JsonPrimitive(0);
                            faceData.add("tintindex", object);
                        }
                    }
                }
                String contentString = GSON.toJson(obj);
                FileWriter writer = new FileWriter(toModify);
                writer.write(contentString);
                writer.close();
                info.setText("Successfully updated the file!");
                info.repaint();
            } catch (Exception e) {
                info.setText("Error occurred while rewriting file - " + e.toString());
                info.repaint();
            }
        });
        frame.add(modify);
        JLabel auth = new JLabel("Created by Toma");
        auth.setBounds(10, 345, 375, 20);
        auth.setVisible(true);
        frame.add(auth);
        frame.setVisible(true);
    }
}
