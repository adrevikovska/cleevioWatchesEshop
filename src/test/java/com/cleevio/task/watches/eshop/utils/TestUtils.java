/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.utils;

import com.cleevio.task.watches.eshop.dto.WatchDTO;
import com.cleevio.task.watches.eshop.model.Watch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Base64Utils;

public final class TestUtils {

    private TestUtils() {
    }

    public static final String BASE_64_IMAGE = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQ"
            + "gKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB"
            + "AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAn/xAAUEAEAAA"
            + "AAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAgP/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwChYAKv/9"
            + "k=";

    public static ResultActions getPerform(MockMvc mockMvc,
                                           MockHttpServletRequestBuilder requestBuilder,
                                           String content) throws Exception {
        return getPerform(mockMvc, requestBuilder, content, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
    }

    public static ResultActions getPerform(MockMvc mockMvc,
                                           MockHttpServletRequestBuilder requestBuilder,
                                           String content,
                                           MediaType contentType,
                                           MediaType accept) throws Exception {
        return mockMvc.perform(requestBuilder
                .characterEncoding("utf-8")
                .content(content)
                .contentType(contentType)
                .accept(accept)
        );
    }

    public static String asJsonString(Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    public static String asXmlString(Object object) throws Exception {
        return new XmlMapper().writer().withRootName("watch").writeValueAsString(object);
    }

    public static WatchDTO createWatchDTO(String title, Integer price, String description, String fountain) {
        return new WatchDTO(
                1L,
                title,
                price,
                description,
                Base64Utils.decodeFromString(fountain)
        );
    }

    public static WatchDTO createWatchDTO() {
        return createWatchDTO("Prim", 250000, "Fountain watch", BASE_64_IMAGE);
    }

    public static Watch createWatchDAO() {
        return new Watch(
                1L,
                "Prim",
                250000,
                "Fountain watch",
                Base64Utils.decodeFromString(BASE_64_IMAGE)
        );
    }

    public static ObjectNode createWatch() {
        return createWatch("Prim", 250000, "Fountain watch", BASE_64_IMAGE);
    }

    public static ObjectNode createWatch(String title, Integer price, String description, String fountain) {
        return JsonNodeFactory.instance.objectNode()
                .put("title", title)
                .put("price", price)
                .put("description", description)
                .put("fountain", fountain);
    }

    public static void assertWatchDTO(ResultActions resultActions,
                                      MediaType contentType,
                                      String title,
                                      Integer price,
                                      String description,
                                      String fountain) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(contentType))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(price))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fountain").value(fountain));
    }

}
