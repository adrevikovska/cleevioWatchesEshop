/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.controller;

import com.cleevio.task.watches.eshop.dto.WatchDTO;
import com.cleevio.task.watches.eshop.dto.WatchDTOOpenApi;
import com.cleevio.task.watches.eshop.service.PatchService;
import com.cleevio.task.watches.eshop.service.WatchService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import javax.json.JsonMergePatch;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.cleevio.task.watches.eshop.utils.RestUtils.checkWatchID;
import static com.cleevio.task.watches.eshop.utils.RestUtils.getWatchDTOWithLinks;
import static com.cleevio.task.watches.eshop.utils.RestUtils.mustExist;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@OpenAPIDefinition(
        info = @Info(
                title = "Watch Eshop REST API documentation",
                version = "1.0",
                description = "Overview of Watch Eshop REST API resources.",
                contact = @Contact(
                        url = "https://github.com/adrevikovska",
                        name = "Anna Drevikovska",
                        email = "anna.drevikovska@gmail.com"
                )
        ),
        servers = { @Server(description = "Watch Eshop server", url = "http://localhost:8080") }
)
@RestController
@RequestMapping(path = "/api/v1/watches")
@AllArgsConstructor
public class WatchController {

    private final WatchService watchService;
    private final PatchService patchService;

    @Operation(summary = "Retrieves all watches")
    @ApiResponse(responseCode = "200", description = "Watches were successfully retrieved.")
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<WatchDTO> getAllWatches() {
        return CollectionModel.of(
                watchService.getAllWatches().stream().map(watchDTO -> getWatchDTOWithLinks(watchDTO.getId(), watchDTO))
                        .collect(Collectors.toList()),
                linkTo(methodOn(WatchController.class).getAllWatches()).withSelfRel()
        );
    }

    @Operation(summary = "Retrieve watch by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watch with id was successfully retrieved."),
            @ApiResponse(responseCode = "400", description = "Invalid id parameter was provided.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Watch with provided id doesn't exist.",
                    content = @Content)
    })
    @GetMapping(value = "{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public WatchDTO getWatchById(@Parameter(description = "Id of the watch to be retrieved.") @PathVariable Long id) {
        return getWatchDTOWithLinks(id, mustExist(watchService.getWatchById(id), id));
    }

    @Operation(summary = "Create a new watch.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = WatchDTOOpenApi.class)),
            @Content(mediaType = MediaType.APPLICATION_XML_VALUE,
                    schema = @Schema(implementation = WatchDTOOpenApi.class))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Watch was successfully created."),
            @ApiResponse(responseCode = "400", description = "Invalid watch was provided.", content = @Content)
    })
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public WatchDTO createWatch(@RequestBody @Valid @NotNull WatchDTO watchDTO) {
        WatchDTO newWatchDTO = watchService.saveWatch(watchDTO);
        return getWatchDTOWithLinks(newWatchDTO.getId(), newWatchDTO);
    }

    @Operation(summary = "Update watch by id.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = WatchDTOOpenApi.class)),
            @Content(mediaType = MediaType.APPLICATION_XML_VALUE,
                    schema = @Schema(implementation = WatchDTOOpenApi.class))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watch was successfully updated."),
            @ApiResponse(responseCode = "404", description = "Watch with provided id doesn't exist.",
                    content = @Content)
    })
    @PutMapping(value = "{id}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public WatchDTO updateWatch(@Parameter(description = "Id of the watch to be updated or created.")
                                    @PathVariable Long id,
                                @RequestBody @Valid @NotNull WatchDTO watchDTO) {
        if (watchDTO.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Watch id must not be null.");
        }
        checkWatchID(id, watchDTO.getId());
        mustExist(watchService.getWatchById(id), id);
        return getWatchDTOWithLinks(id, watchService.saveWatch(watchDTO));
    }

    @Operation(summary = "Update watch by id.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(mediaType = "application/merge-patch+json",
                    schema = @Schema(implementation = WatchDTOOpenApi.class)),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watch was successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid watch or id parameter was provided.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Watch with provided id doesn't exist.",
                    content = @Content)
    })
    @PatchMapping(value = "{id}", consumes = "application/merge-patch+json",
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public WatchDTO patchWatch(@Parameter(description = "Id of the watch to be updated.") @PathVariable Long id,
                               @RequestBody @NotNull JsonMergePatch patch) {
        WatchDTO watchDTO = mustExist(watchService.getWatchById(id), id);
        WatchDTO patchedWatchDTO = patchService.applyPatch(patch, watchDTO, WatchDTO.class);
        checkWatchID(id, patchedWatchDTO.getId());
        watchService.saveWatch(patchedWatchDTO);
        return getWatchDTOWithLinks(id, patchedWatchDTO);
    }

    @Operation(summary = "Delete watch by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Watch was successfully deleted."),
            @ApiResponse(responseCode = "400", description = "Invalid id parameter was provided.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Watch with provided id doesn't exist.",
                    content = @Content)
    })
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWatch(@Parameter(description = "Id of the watch to be deleted.") @PathVariable Long id) {
        mustExist(watchService.getWatchById(id), id);
        watchService.deleteWatchById(id);
    }

}
