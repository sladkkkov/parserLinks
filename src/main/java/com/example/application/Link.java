package com.example.application;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Link {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(name, link.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    private Long id;
    private String name;

    public Link(String name, Integer size) {
        this.name = name;
        this.size = size;
    }

    private Integer size;
}
