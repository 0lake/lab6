package com.general.managers;

import com.general.models.base.Element;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Абстрактный класс для управления коллекцией обобщенного типа.
 */
public abstract class CollectionManager<T extends Element & Comparable<T>> {
    private Collection<T> collection;
    @Getter
    @Setter
    private LocalDateTime lastInitTime;
    @Getter
    @Setter
    private LocalDateTime lastSaveTime;
    @Setter
    @Getter
    private long nextId = 1; // Initial nextId

    private static final Logger logger = LoggerFactory.getLogger(CollectionManager.class);

    public CollectionManager() {
        this.collection = createCollection();
        this.lastInitTime = null;
        this.lastSaveTime = null;
    }

    /**
     * Метод для создания коллекции. Должен быть реализован в классах-наследниках.
     *
     * @return пустая коллекция нужного типа
     */
    protected abstract Collection<T> createCollection();

    /**
     * Метод для загрузки коллекции. Должен быть реализован в классах-наследниках.
     */
    protected abstract void loadCollection();

    /**
     * Метод для сохранения коллекции. Должен быть реализован в классах-наследниках.
     */
    public abstract void saveCollection();

    public void validateAll() {
        Collection<T> validCollection = createCollection();
        boolean isValid = true;
        Set<Long> existingIds = new HashSet<>();
        Set<Long> duplicateIds = new HashSet<>();

        for (T element : collection) {
            if (!element.validate()) {
                logger.error("Элемент с id={} имеет невалидные поля. Проигнорирован.", getId(element));
                isValid = false;
            } else if (existingIds.contains(getId(element))) {
                duplicateIds.add(getId(element));
            } else {
                existingIds.add(getId(element));
                validCollection.add(element);
            }
        }

        if (!isValid || !duplicateIds.isEmpty()) {
            collection = validCollection;
            if (!isValid) {
                logger.info("Имеются невалидные элементы. Коллекция изменена.");
            }
            for (Long id : duplicateIds) {
                logger.error("Элемент с id={} уже существует. Проигнорирован.", id);
            }
        } else {
            logger.info("Все элементы коллекции валидны.");
            collection = validCollection;
        }
    }

    /**
     * Проверяет, существует ли элемент с такими же значениями полей в коллекции.
     *
     * @param element Элемент для проверки.
     * @return true, если элемент существует, иначе false.
     */
    public boolean checkExistInCollection(T element) {
        return collection.contains(element);
    }

    /**
     * Проверяет, существует ли элемент с таким ID.
     *
     * @param id ID элемента.
     * @return true, если элемент существует, иначе false.
     */
    public boolean checkExist(Long id) {
        for (T element : collection) {
            if (getId(element).equals(id)) return true;
        }
        return false;
    }

    /**
     * Возвращает коллекцию.
     *
     * @return коллекция.
     */
    public Collection<T> getCollection() {
        return collection;
    }
    /**
     * Возвращает имя типа коллекции.
     *
     * @return имя типа коллекции.
     */
    public String collectionType() {
        return collection.getClass().getName();
    }

    /**
     * Возвращает размер коллекции.
     *
     * @return размер коллекции.
     */
    public int collectionSize() {
        return collection.size();
    }

    /**
     * Возвращает последний элемент коллекции (null, если коллекция пустая).
     *
     * @return последний элемент коллекции или null.
     */
    public T getLast() {
        if (collection.isEmpty()) return null;
        T lastElement = null;
        for (T element : collection) {
            lastElement = element;
        }
        return lastElement;
    }

    /**
     * Возвращает элемент по его ID или null, если не найдено.
     *
     * @param id ID элемента.
     * @return элемент по его ID или null.
     */
    public T getById(Long id) {
        for (T element : collection) {
            if (getId(element).equals(id)) return element;
        }
        return null;
    }

    /**
     * Добавляет элемент в коллекцию.
     *
     * @param element элемент для добавления.
     * @return новый ID элемента.
     */
    public Long addToCollection(T element) {
        element.setId(nextId++);
        collection.add(element);
        sortCollection(); // Сортировка после добавления элемента
        return element.getId();
    }

    /**
     * Удаляет элемент из коллекции.
     *
     * @param element элемент для удаления.
     */
    public void removeFromCollection(T element) {
        collection.remove(element);
        sortCollection(); // Сортировка после удаления элемента
    }

    /**
     * Очищает коллекцию.
     */
    public void clearCollection() {
        collection.clear();
    }

    /**
     * Метод для получения ID элемента. Должен быть реализован в классах-наследниках.
     *
     * @param element элемент коллекции.
     * @return ID элемента.
     */
    protected abstract Long getId(T element);

    /**
     * Сортирует коллекцию по имени.
     */
    public void sortCollection() {
        Collection<T> sortedCollection = collection.stream()
                .sorted(Comparator.comparing(T::getName))
                .collect(Collectors.toList());
        setCollection(sortedCollection);
    }

    /**
     * Устанавливает коллекцию.
     *
     * @param collection новая коллекция
     */
    private void setCollection(Collection<T> collection) {
        this.collection.clear();
        this.collection.addAll(collection);
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "Коллекция пуста!";
        var last = getLast();

        StringBuilder info = new StringBuilder();
        for (T element : collection) {
            info.append(element);
            if (element != last) info.append("\n\n");
        }
        return info.toString();
    }
}
