/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

// Workaround for Swagger using HATEOAS links in request bodies
@Data
public class WatchDTOOpenApi {

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

}
