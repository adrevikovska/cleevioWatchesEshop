/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.service;

import com.cleevio.task.watches.eshop.dto.WatchDTO;

import java.util.List;

public interface WatchService {

    List<WatchDTO> getAllWatches();

    WatchDTO getWatchById(Long id);

    WatchDTO saveWatch(WatchDTO watchDTO);

    void deleteWatchById(Long id);

}
