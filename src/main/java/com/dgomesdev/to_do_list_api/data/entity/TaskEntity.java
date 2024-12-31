package com.dgomesdev.to_do_list_api.data.entity;

import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "tb_tasks")
@Getter
public class TaskEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Setter
        @Column(nullable = false)
        private String title;

        @Setter
        private String description;

        @Setter
        @Column(nullable = false)
        private Priority priority = Priority.MEDIUM;

        @Setter
        @Column(nullable = false)
        private Status status = Status.TO_BE_DONE;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private UserEntity user;

        @CreationTimestamp
        @Column(updatable = false,name = "created_at")
        private Date createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private Date updatedAt;

        public TaskEntity(TaskModel task, UserEntity user) {
                this.title = task.getTitle();
                this.description = task.getDescription();
                this.priority = task.getPriority();
                this.status = task.getStatus();
                this.user = user;
        }

        protected TaskEntity() {}
}