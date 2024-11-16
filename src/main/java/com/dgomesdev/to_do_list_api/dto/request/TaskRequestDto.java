package com.dgomesdev.to_do_list_api.dto.request;

import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import jakarta.validation.constraints.NotBlank;

public record TaskRequestDto(
   @NotBlank String title,
   String description,
   Priority priority,
   Status status
) {}