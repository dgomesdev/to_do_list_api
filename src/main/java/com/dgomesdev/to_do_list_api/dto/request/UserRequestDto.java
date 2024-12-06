package com.dgomesdev.to_do_list_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
        @NotBlank String username,
        String email,
        String password
) {}