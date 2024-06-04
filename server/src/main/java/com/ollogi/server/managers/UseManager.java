package com.ollogi.server.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.general.io.DateAdapter;
import com.general.models.Flat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * Класс UseManager предназначен для сохранения и загрузки коллекции объектов Flat из файла.
 */
public class UseManager {
    /** Объект Gson для сериализации и десериализации объектов в JSON формат. */
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDate.class, new DateAdapter())
            .create();

    /** Имя файла, в который производится сохранение и из которого производится загрузка коллекции. */
    private final String fileName;

    /** Логгер для вывода информации о состоянии операций. */
    private static final Logger logger = LoggerFactory.getLogger(UseManager.class);

    /**
     * Конструктор класса UseManager.
     * @param fileName Имя файла для сохранения и загрузки коллекции.
     */
    public UseManager(String fileName) {
        // Проверка, существует ли файл. Если нет, то добавляем префикс "../".
        if (!(new File(fileName).exists())) {
            fileName = "../" + fileName;
        }
        this.fileName = fileName;
    }

    /**
     * Записывает коллекцию в файл.
     * @param collection Коллекция объектов Flat, которую необходимо сохранить в файл.
     */
    public void writeCollection(Collection<Flat> collection) {
        try (PrintWriter collectionPrintWriter = new PrintWriter(new File(fileName))) {
            // Сериализация коллекции в JSON формат и запись в файл.
            collectionPrintWriter.println(gson.toJson(collection));
            logger.info("Коллекция успешно сохранена в файл!");
        } catch (IOException exception) {
            logger.error("Загрузочный файл не может быть открыт!", exception);
        }
    }

    /**
     * Считывает коллекцию из файла.
     * @return Считанная коллекция объектов Flat.
     */
    public Collection<Flat> readCollection() {
        if (fileName != null && !fileName.isEmpty()) {
            try (var fileReader = new FileReader(fileName)) {
                var collectionType = new TypeToken<PriorityQueue<Flat>>() {}.getType();
                var reader = new BufferedReader(fileReader);

                var jsonString = new StringBuilder();

                String line;
                // Чтение файла построчно и запись содержимого в StringBuilder.
                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.equals("")) {
                        jsonString.append(line);
                    }
                }

                // Если файл пуст, создается пустая коллекция.
                if (jsonString.length() == 0) {
                    jsonString = new StringBuilder("[]");
                }

                // JSON строки в коллекцию объектов Flat.
                PriorityQueue<Flat> collection = gson.fromJson(jsonString.toString(), collectionType);

                logger.info("Коллекция успешно загружена!");
                return collection;

            } catch (FileNotFoundException exception) {
                logger.error("Загрузочный файл не найден!", exception);
            } catch (NoSuchElementException exception) {
                logger.error("Загрузочный файл пуст!", exception);
            } catch (JsonParseException exception) {
                logger.error("В загрузочном файле не обнаружена необходимая коллекция!", exception);
            } catch (IllegalStateException | IOException exception) {
                logger.error("Непредвиденная ошибка!", exception);
                System.exit(0);
            }
        } else {
            logger.error("Аргумент командной строки с загрузочным файлом не найден!");
        }
        // Возвращение пустой коллекции в случае возникновения ошибок.
        return new PriorityQueue<>();
    }
}
