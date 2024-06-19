package com.general.models.base;

import java.io.Serializable;

public abstract class Element implements Validatable, Serializable {
    private Long id;
    public abstract String getName();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
