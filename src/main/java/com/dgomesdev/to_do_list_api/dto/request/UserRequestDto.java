package com.dgomesdev.to_do_list_api.dto.request;

public record UserRequestDto(
        String username,
        String email,
        String password
) {}