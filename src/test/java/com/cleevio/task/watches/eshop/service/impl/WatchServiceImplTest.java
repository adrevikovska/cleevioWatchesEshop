/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.service.impl;

import com.cleevio.task.watches.eshop.dto.WatchDTO;
import com.cleevio.task.watches.eshop.mapper.WatchMapper;
import com.cleevio.task.watches.eshop.model.Watch;
import com.cleevio.task.watches.eshop.repository.WatchRepository;
import com.cleevio.task.watches.eshop.service.WatchService;
import com.cleevio.task.watches.eshop.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WatchServiceImplTest {

    @Mock
    private WatchRepository watchRepository;

    @Mock
    private WatchMapper watchMapper;

    private WatchService watchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        watchService = new WatchServiceImpl(watchRepository, watchMapper);
    }

    @Test
    void getAllWatches() {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        Watch watch = TestUtils.createWatchDAO();
        when(watchRepository.findAll()).thenReturn(Collections.singletonList(watch));
        when(watchMapper.watchToWatchDTO(eq(watch))).thenReturn(watchDTO);
        List<WatchDTO> watches = watchService.getAllWatches();
        assertThat(watches.size()).isEqualTo(1);
        assertThat(watches.contains(watchDTO)).isTrue();
        verify(watchMapper).watchToWatchDTO(eq(watch));
        verify(watchRepository).findAll();
    }

    @Test
    void getWatchById() {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        Watch watch = TestUtils.createWatchDAO();
        when(watchRepository.findById(eq(1L))).thenReturn(Optional.of(watch));
        when(watchMapper.watchToWatchDTO(eq(watch))).thenReturn(watchDTO);
        WatchDTO retrievedWatchDTO = watchService.getWatchById(1L);
        assertThat(retrievedWatchDTO.equals(watchDTO)).isTrue();
        verify(watchMapper).watchToWatchDTO(eq(watch));
        verify(watchRepository).findById(eq(1L));
    }

    @Test
    void saveWatch() {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        Watch watch = TestUtils.createWatchDAO();
        when(watchRepository.save(eq(watch))).thenReturn(watch);
        when(watchMapper.watchToWatchDTO(eq(watch))).thenReturn(watchDTO);
        when(watchMapper.watchDTOToWatch(eq(watchDTO))).thenReturn(watch);
        WatchDTO retrievedWatchDTO = watchService.saveWatch(watchDTO);
        assertThat(retrievedWatchDTO.equals(watchDTO)).isTrue();
        verify(watchMapper).watchDTOToWatch(eq(watchDTO));
        verify(watchMapper).watchToWatchDTO(eq(watch));
        verify(watchRepository).save(eq(watch));
    }

    @Test
    void deleteWatchById() {
        watchService.deleteWatchById(1L);
        verify(watchRepository).deleteById(eq(1L));
    }

}
