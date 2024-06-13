package com.ollogi.server.managers;

import com.general.managers.CollectionManager;
import com.general.models.Flat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Оперирует коллекцией объектов Flat.
 */
public class FlatCollectionManager extends CollectionManager<Flat> {
    private final UseManager useManager;

    public FlatCollectionManager(UseManager useManager) {
        this.useManager = useManager;
        loadCollection();
    }

    @Override
    protected Collection<Flat> createCollection() {
        return new PriorityQueue<>();
    }

    @Override
    protected void loadCollection() {
        Collection<Flat> loadedCollection = useManager.readCollection();

        // Sort the collection by name
        Collection<Flat> sortedCollection = loadedCollection.stream()
                .sorted(Comparator.comparing(Flat::getName))
                .collect(Collectors.toList());

        setCollection(sortedCollection);
        setLastInitTime(LocalDateTime.now());

        // Устанавливаем nextId на 1 больше максимального ID в коллекции
        Optional<Long> maxId = sortedCollection.stream()
                .map(flat -> flat.getId())
                .max(Long::compareTo);
        setNextId(maxId.orElse(0L) + 1);
    }

    @Override
    public void saveCollection() {
        useManager.writeCollection(getCollection());
        setLastSaveTime(LocalDateTime.now());
    }

    @Override
    protected Long getId(Flat element) {
        return element.getId();
    }

    /**
     * Устанавливает коллекцию.
     *
     * @param collection новая коллекция
     */
    private void setCollection(Collection<Flat> collection) {
        super.getCollection().clear();
        super.getCollection().addAll(collection);
    }
}
