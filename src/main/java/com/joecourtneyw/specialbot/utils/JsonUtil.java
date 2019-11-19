package com.joecourtneyw.specialbot.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonUtil {

    public static JsonNode getJsonFile(File file) {
        try {
            byte[] jsonData = Files.readAllBytes(file.toPath());
            return new ObjectMapper().readTree(jsonData);
        } catch (IOException ioe) {
            LoggerUtil.CRITICAL("Failed to load json file: " + file.getAbsolutePath());
            return null;
        }
    }

    public static void updateJsonFile(File jsonFile, Object jsonObject) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(jsonFile, jsonObject);
        } catch (IOException e) {
            LoggerUtil.CRITICAL("Failed to update json file: " + jsonFile.getAbsolutePath());
        }
    }

    public static Object getJavaObject(File jsonFile, Class<?> javaClass) {
        try {
            return new ObjectMapper().readValue(jsonFile, javaClass);
        } catch (IOException e) {
            LoggerUtil.CRITICAL("Failed to load json file: " + jsonFile.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
    }

}
