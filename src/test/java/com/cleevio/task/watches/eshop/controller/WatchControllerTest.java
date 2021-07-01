/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.controller;

import com.cleevio.task.watches.eshop.config.JsonMergePatchHttpMessageConverter;
import com.cleevio.task.watches.eshop.dto.WatchDTO;
import com.cleevio.task.watches.eshop.service.PatchService;
import com.cleevio.task.watches.eshop.service.WatchService;
import com.cleevio.task.watches.eshop.utils.TestUtils;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static com.cleevio.task.watches.eshop.utils.TestUtils.BASE_64_IMAGE;
import static com.cleevio.task.watches.eshop.utils.TestUtils.asJsonString;
import static com.cleevio.task.watches.eshop.utils.TestUtils.assertWatchDTO;
import static com.cleevio.task.watches.eshop.utils.TestUtils.createWatchDTO;
import static com.cleevio.task.watches.eshop.utils.TestUtils.getPerform;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WatchControllerTest {

    private static final String BASE_URL = "/api/v1/watches";
    private static final String WATCH_URL_TEMPLATE = BASE_URL + "/{id}";

    @Mock
    private WatchService watchService;

    @Mock
    private PatchService patchService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new WatchController(watchService, patchService))
                .setMessageConverters(
                        new JsonMergePatchHttpMessageConverter(),
                        new MappingJackson2XmlHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter()
                ).build();
    }

    @Test
    void getAllWatches() throws Exception {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        when(watchService.getAllWatches()).thenReturn(Collections.singletonList(watchDTO));
        mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].title").value("Prim"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].description")
                        .value("Fountain watch"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].price").value(250000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].fountain").value(BASE_64_IMAGE));
        verify(watchService).getAllWatches();
    }

    @Test
    void getWatchById() throws Exception {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        when(watchService.getWatchById(eq(1L))).thenReturn(watchDTO);
        ResultActions actions = mockMvc.perform(get(WATCH_URL_TEMPLATE, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        TestUtils.assertWatchDTO(actions, MediaType.APPLICATION_JSON, "Prim", 250000,
                "Fountain watch", BASE_64_IMAGE);
        verify(watchService).getWatchById(eq(1L));
    }

    @Test
    void createWatch() throws Exception {
        ObjectNode watch = TestUtils.createWatch();
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        when(watchService.saveWatch(any())).thenReturn(watchDTO);
        ResultActions actions = getPerform(mockMvc, post(BASE_URL), asJsonString(watch))
                .andExpect(status().isCreated());
        TestUtils.assertWatchDTO(actions, MediaType.APPLICATION_JSON, "Prim", 250000,
                "Fountain watch", BASE_64_IMAGE);
        verify(watchService).saveWatch(any());
    }

    @Test
    void updateWatch() throws Exception {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        WatchDTO updatedWatchDTO = TestUtils.createWatchDTO("Prime", 250, "Watch", BASE_64_IMAGE);
        when(watchService.getWatchById(eq(1L))).thenReturn(watchDTO);
        when(watchService.saveWatch(any())).thenReturn(updatedWatchDTO);
        ResultActions actions = getPerform(mockMvc, put(WATCH_URL_TEMPLATE, 1L), asJsonString(updatedWatchDTO))
                .andExpect(status().isOk());
        TestUtils.assertWatchDTO(actions, MediaType.APPLICATION_JSON, "Prime", 250, "Watch",
                BASE_64_IMAGE);
    }

    @Test
    void patchWatch() throws Exception {
        ObjectNode patchWatch = TestUtils.createWatch("Prime", 250, "Watch", BASE_64_IMAGE);
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        WatchDTO patchedWatchDTO = TestUtils.createWatchDTO("Prime", 250, "Watch", BASE_64_IMAGE);
        when(watchService.getWatchById(eq(1L))).thenReturn(watchDTO);
        when(patchService.applyPatch(any(), any(), eq(WatchDTO.class))).thenReturn(patchedWatchDTO);
        when(watchService.saveWatch(any())).thenReturn(patchedWatchDTO);
        ResultActions actions = getPerform(mockMvc, patch(WATCH_URL_TEMPLATE, 1L), patchWatch.toString(),
                MediaType.valueOf("application/merge-patch+json"), MediaType.APPLICATION_JSON)
                .andExpect(status().isOk());
        TestUtils.assertWatchDTO(actions, MediaType.APPLICATION_JSON, "Prime", 250, "Watch",
                BASE_64_IMAGE);
        verify(patchService).applyPatch(any(), any(), eq(WatchDTO.class));
        verify(watchService).saveWatch(eq(patchedWatchDTO));
    }

    @Test
    void deleteWatch() throws Exception {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        when(watchService.getWatchById(eq(1L))).thenReturn(watchDTO);
        mockMvc.perform(delete(WATCH_URL_TEMPLATE, 1)).andExpect(status().isNoContent());
        verify(watchService).deleteWatchById(eq(1L));
    }

    @Test
    void createAndAcceptWatchXml() throws Exception {
        String fountainResult = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkI\n"
                + "CQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQ\n"
                + "EBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAABAAEDASIA\n"
                + "AhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAn/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEB\n"
                + "AQAAAAAAAAAAAAAAAAAAAgP/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwChYAKv\n"
                + "/9k=";

        WatchDTO watchDTO = TestUtils.createWatchDTO();
        ObjectNode watch = TestUtils.createWatch();
        String watchXml = TestUtils.asXmlString(watch);
        when(watchService.saveWatch(any())).thenReturn(watchDTO);
        mockMvc.perform(post(BASE_URL)
                .characterEncoding("utf-8")
                .content(watchXml)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
        ).andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_XML))
                .andExpect(MockMvcResultMatchers.xpath("/watch/id/text()").string("1"))
                .andExpect(MockMvcResultMatchers.xpath("/watch/title/text()").string("Prim"))
                .andExpect(MockMvcResultMatchers.xpath("/watch/price/text()").string("250000"))
                .andExpect(MockMvcResultMatchers.xpath("/watch/description/text()")
                        .string("Fountain watch"))
                .andExpect(MockMvcResultMatchers.xpath("/watch/fountain/text()").string(fountainResult));
    }

    @Test
    void updateFountainImage() throws Exception {
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        ObjectNode watch = TestUtils.createWatch();
        when(watchService.saveWatch(any())).thenReturn(watchDTO);
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isCreated());
        when(watchService.getWatchById(1L)).thenReturn(watchDTO);

        String imageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgA"
                + "AgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QA/4ePzL8AAAAHdElNRQflBh4RKSGgELNvAAAAC0lEQVQI12P"
                + "g0wIAAEkAOXq0VaMAAAAldEVYdGRhdGU6Y3JlYXRlADIwMjEtMDYtMzBUMTc6NDE6MzMrMDA6MDDV8rBJAAAAJXRFWHRkYXRlOm1"
                + "vZGlmeQAyMDIxLTA2LTMwVDE3OjQxOjMzKzAwOjAwpK8I9QAAAABJRU5ErkJggg==";

        watch.put("fountain", imageBase64);
        watch.put("id", 1);
        WatchDTO updatedWatchDTO = TestUtils.createWatchDTO("Prim", 250000, "Fountain watch",
                imageBase64);
        when(watchService.saveWatch(any())).thenReturn(updatedWatchDTO);
        ResultActions actions = getPerform(mockMvc, put(WATCH_URL_TEMPLATE, 1L), watch.toString())
                .andExpect(status().isOk());
        assertWatchDTO(actions, MediaType.APPLICATION_JSON, "Prim", 250000, "Fountain watch",
                imageBase64);
    }

    @Test
    void invalidTitle() throws Exception {
        ObjectNode watch = TestUtils.createWatch();
        watch.put("title", "   ");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
        watch.remove("title");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
        watch.put("title", "pri");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
    }

    @Test
    void invalidPrice() throws Exception {
        ObjectNode watch = TestUtils.createWatch();
        watch.put("price", 0);
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
        watch.remove("price");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
    }

    @Test
    void invalidDescription() throws Exception {
        ObjectNode watch = TestUtils.createWatch();
        watch.put("description", "    ");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
        watch.remove("description");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
    }

    @Test
    void invalidFountain() throws Exception {
        ObjectNode watch = TestUtils.createWatch();
        watch.put("fountain", "R0lGODlhAQABAIAAAAUEBA");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
        watch.remove("fountain");
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isBadRequest());
    }

    @Test
    void invalidUpdate() throws Exception {
        ObjectNode watch = TestUtils.createWatch();
        WatchDTO watchDTO = TestUtils.createWatchDTO();
        when(watchService.saveWatch(any())).thenReturn(watchDTO);
        getPerform(mockMvc, post(BASE_URL), watch.toString()).andExpect(status().isCreated());
        watch.put("id", 2);
        getPerform(mockMvc, put(WATCH_URL_TEMPLATE, 1L), watch.toString())
                .andExpect(status().isUnprocessableEntity());
        watch.remove("id");
        getPerform(mockMvc, put(WATCH_URL_TEMPLATE, 1L), watch.toString())
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidRequestBody() throws Exception {
        String content = "";
        getPerform(mockMvc, post(BASE_URL), content).andExpect(status().isBadRequest());
        getPerform(mockMvc, put(WATCH_URL_TEMPLATE, 1L), content).andExpect(status().isBadRequest());
        getPerform(mockMvc, patch(WATCH_URL_TEMPLATE, 1L), content,
                MediaType.valueOf("application/merge-patch+json"), MediaType.APPLICATION_JSON)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWatchThatDoesntExist() throws Exception {
        mockMvc.perform(get(WATCH_URL_TEMPLATE, 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(watchService).getWatchById(eq(1L));
    }

    @Test
    void updateWatchThatDoesntExist() throws Exception {
        WatchDTO updatedWatchDTO = createWatchDTO("Prime", 250, "Watch", BASE_64_IMAGE);
        getPerform(mockMvc, put(WATCH_URL_TEMPLATE, 1L), asJsonString(updatedWatchDTO))
                .andExpect(status().isNotFound());
        verify(watchService).getWatchById(eq(1L));
    }

    @Test
    void patchWatchThatDoesntExist() throws Exception {
        ObjectNode watch = JsonNodeFactory.instance.objectNode()
                .put("title", "Prime");
        getPerform(mockMvc, patch(WATCH_URL_TEMPLATE, 1L), watch.toString(),
                MediaType.valueOf("application/merge-patch+json"), MediaType.APPLICATION_JSON)
                .andExpect(status().isNotFound());
        verify(watchService).getWatchById(eq(1L));
    }

    @Test
    void deleteWatchThatDoesntExist() throws Exception {
        mockMvc.perform(delete(WATCH_URL_TEMPLATE, 1L)).andExpect(status().isNotFound());
        verify(watchService).getWatchById(eq(1L));
    }

}
