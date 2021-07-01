/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@EqualsAndHashCode
@ToString
public class Watch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private Integer price;

    @Column
    private String description;

    @Lob
    @Column
    private byte[] fountain;

    public Watch() {
    }

    public Watch(Long id,
                 String title,
                 Integer price,
                 String description,
                 byte[] fountain) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.fountain = fountain;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFountain() {
        return fountain;
    }

    public void setFountain(byte[] fountain) {
        this.fountain = fountain;
    }

}
