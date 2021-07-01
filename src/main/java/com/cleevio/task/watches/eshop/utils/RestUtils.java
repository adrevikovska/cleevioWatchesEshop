/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.utils;

import com.cleevio.task.watches.eshop.controller.WatchController;
import com.cleevio.task.watches.eshop.dto.WatchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
public final class RestUtils {

    private static final String NOT_FOUND = "Watch with id %s doesn't exist.";

    private RestUtils() {
    }

    public static <T> T mustExist(T object, Long id) {
        if (object == null) {
            log.debug("Watch with id {} doesn't exist.", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(NOT_FOUND, id));
        }
        return object;
    }

    public static void checkWatchID(Long expected, Long actual) {
        if (!expected.equals(actual)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Watch id must match id specified in path."
            );
        }
    }

    public static WatchDTO getWatchDTOWithLinks(Long id, WatchDTO watchDTO) {
        return watchDTO.add(
                linkTo(WatchController.class).slash(id).withSelfRel(),
                linkTo(methodOn(WatchController.class).getAllWatches()).withRel("watches")
        );
    }

}
