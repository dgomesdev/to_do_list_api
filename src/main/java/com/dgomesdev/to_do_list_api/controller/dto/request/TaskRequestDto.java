package com.dgomesdev.to_do_list_api.controller.dto.request;

public record TaskRequestDto(
   String title,
   String description,
   String priority,
   String status
) {}
