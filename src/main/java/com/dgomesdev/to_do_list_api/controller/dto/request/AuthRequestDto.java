package com.dgomesdev.to_do_list_api.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto(
        @NotBlank
        String username,
        @NotBlank
        String password
) {}