package com.ollogi.server.managers;

import com.general.managers.CollectionManager;
import com.general.models.Flat;
import com.general.models.base.Element;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
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
        setCollection(loadedCollection);
        sortCollection();  // Сортировка коллекции при загрузке
        setLastInitTime(LocalDateTime.now());

        Long maxId = getCollection().stream()
                .mapToLong(Flat::getId)
                .max()
                .orElse(0L);
        setNextId(maxId + 1);
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

    private void setCollection(Collection<Flat> collection) {
        super.getCollection().clear();
        super.getCollection().addAll(collection);
    }

    @Override
    public Long addToCollection(Flat element) {
        Long id = super.addToCollection(element);
        sortCollection();
        return id;
    }

    @Override
    public void removeFromCollection(Flat element) {
        super.removeFromCollection(element);
        sortCollection();
    }

    /**
     * Сортирует коллекцию по имени.
     */
    @Override
    public void sortCollection() {
        setCollection(
                getCollection().stream()
                        .sorted(Comparator.comparing(Flat::getName))
                        .collect(Collectors.toCollection(PriorityQueue::new))
        );
    }
}
