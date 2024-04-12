package com.dgomesdev.to_do_list_api.controller.dto.request;

public record AuthRequestDto(
        String username,
        String password
) {}