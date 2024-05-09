package com.dgomesdev.to_do_list_api.controller.dto.response;

import java.util.List;

public record AllTasksResponseDto(
        List<TaskResponseDto> tasks
) implements ResponseDto {}
