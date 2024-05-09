package com.dgomesdev.to_do_list_api.data.repository;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TaskRepositoryTest {

    @Mock
    TaskRepository taskRepository;

    @Test
    @DisplayName("Should return the list with all tasks successfully")
    void givenValidUserId_whenFindingAllTasksByUserId_thenReturnListOfAllTasks() {
        //GIVEN
        UUID mockUserId = UUID.randomUUID();
        var tasks = List.of(
                new TaskEntity(
                        UUID.randomUUID(),
                        "title1",
                        "description1",
                        Priority.LOW,
                        Status.TO_BE_DONE,
                        mockUserId
                )
        );
        when(taskRepository.findTasksByUserId(mockUserId)).thenReturn(tasks);

        //WHEN
        var result = taskRepository.findTasksByUserId(mockUserId);

        //THEN
        assertThat(result).isEqualTo(tasks);
    }
    @Test
    @DisplayName("Should return an empty list of tasks when user doesn't exist")
    void givenInvalidUserId_whenFindingAllTasksByUserId_thenReturnEmptyList() {
        //GIVEN
        UUID nonExistentUserId = UUID.randomUUID();

        //WHEN
        var result = taskRepository.findTasksByUserId(nonExistentUserId);

        //THEN
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        assertThat(result).isEqualTo(List.of());
    }
}