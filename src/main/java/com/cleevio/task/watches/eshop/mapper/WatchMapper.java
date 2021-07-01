/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.mapper;

import com.cleevio.task.watches.eshop.dto.WatchDTO;
import com.cleevio.task.watches.eshop.model.Watch;
import org.mapstruct.Mapper;

@Mapper
public interface WatchMapper {

    WatchDTO watchToWatchDTO(Watch watch);

    Watch watchDTOToWatch(WatchDTO watchDTO);

}
