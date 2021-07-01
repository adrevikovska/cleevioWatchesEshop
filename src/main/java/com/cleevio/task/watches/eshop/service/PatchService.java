/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.service;

import javax.json.JsonMergePatch;

public interface PatchService {

    <T> T applyPatch(JsonMergePatch jsonMergePatch, T targetBean, Class<T> clazz);

}
