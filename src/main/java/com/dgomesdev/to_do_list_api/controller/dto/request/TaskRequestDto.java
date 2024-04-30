package com.dgomesdev.to_do_list_api.controller.dto.request;

import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequestDto(
   @NotBlank
   String title,
   @NotNull
   String description,
   @NotNull
   Priority priority,
   @NotNull
   Status status
) {}
