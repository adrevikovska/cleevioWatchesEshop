/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.service.impl;

import com.cleevio.task.watches.eshop.service.PatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

@Service
@AllArgsConstructor
@Slf4j
public class PatchServiceImpl implements PatchService {

    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public <T> T applyPatch(JsonMergePatch jsonMergePatch, T targetBean, Class<T> clazz) {
        // Convert the Java bean to a JSON document
        JsonValue target = objectMapper.convertValue(targetBean, JsonValue.class);

        // Apply the JSON Merge Patch to the JSON document
        JsonValue patched = jsonMergePatch.apply(target);

        // Convert the JSON document to a Java bean and return it
        T patchedBean = objectMapper.convertValue(patched, clazz);

        Set<ConstraintViolation<T>> violations = validator.validate(patchedBean);
        if (!violations.isEmpty()) {
            log.debug("Validation of patched bean has failed.");
            throw new ConstraintViolationException(violations);
        }

        return patchedBean;
    }

}
