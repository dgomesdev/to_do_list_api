package com.dgomesdev.to_do_list_api.dto.response;

public record AuthResponseDto(
        UserResponseDto user,
        String token
) {}
