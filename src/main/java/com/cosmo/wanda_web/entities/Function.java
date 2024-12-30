package com.cosmo.wanda_web.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_function")
public class Function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(columnDefinition = "TEXT")
    private String function;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private User player;

    public Function() {
    }

    public Function(Long id, String name, String function) {
        this.id = id;
        this.name = name;
        this.function = function;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Function function = (Function) o;

        return Objects.equals(id, function.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
