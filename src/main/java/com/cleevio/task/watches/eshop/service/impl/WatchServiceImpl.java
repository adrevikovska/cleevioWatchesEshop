/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.service.impl;

import com.cleevio.task.watches.eshop.dto.WatchDTO;
import com.cleevio.task.watches.eshop.mapper.WatchMapper;
import com.cleevio.task.watches.eshop.repository.WatchRepository;
import com.cleevio.task.watches.eshop.service.WatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class WatchServiceImpl implements WatchService {

    private final WatchRepository watchRepository;
    private final WatchMapper watchMapper;

    @Override
    public List<WatchDTO> getAllWatches() {
        return watchRepository.findAll().stream().map(watchMapper::watchToWatchDTO).collect(Collectors.toList());
    }

    @Override
    public WatchDTO getWatchById(Long id) {
        return watchMapper.watchToWatchDTO(watchRepository.findById(id).orElse(null));
    }

    @Transactional
    @Override
    public WatchDTO saveWatch(WatchDTO watchDTO) {
        WatchDTO createdWatch = watchMapper.watchToWatchDTO(
                watchRepository.save(watchMapper.watchDTOToWatch(watchDTO))
        );
        log.debug("Watch with id {} was successfully created or updated.", createdWatch.getId());
        return createdWatch;
    }

    @Override
    public void deleteWatchById(Long id) {
        log.debug("Watch with id {} was successfully removed.", id);
        watchRepository.deleteById(id);
    }

}
