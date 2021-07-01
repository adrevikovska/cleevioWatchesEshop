/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.dto;

import com.cleevio.task.watches.eshop.mapper.annotation.Default;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@JacksonXmlRootElement(localName = "watch")
@ToString
@EqualsAndHashCode(callSuper = false)
public class WatchDTO extends RepresentationModel<WatchDTO> {

    private final Long id;

    @NotBlank
    @Size(min = 4, max = 256)
    private final String title;

    @NotNull
    @Positive
    private final Integer price;

    @NotBlank
    @Size(min = 1, max = 256)
    private final String description;

    @NotNull
    private final byte[] fountain;

    @JsonCreator
    public WatchDTO(@JsonProperty("title") String title,
                    @JsonProperty("price") Integer price,
                    @JsonProperty("description") String description,
                    @JsonProperty("fountain") byte[] fountain) {
        this(null, title, price, description, fountain);
    }

    @Default
    public WatchDTO(Long id, String title, Integer price, String description, byte[] fountain) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.fountain = fountain;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getFountain() {
        return fountain;
    }

}
