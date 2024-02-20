package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.Task;
import com.technofacts.lnf.company.model.enums.TaskStatus;
import com.technofacts.lnf.dto.company.TaskDto;

public class TaskConverter {

    public static TaskDto toTransportModel(Task entity) {

        if (entity == null) {
            return null;
        }

        return TaskDto.builder()
        		.id(entity.getId())
        		.title(entity.getTitle())
        		.description(entity.getDescription())
        		.dueDate(entity.getDueDate())
        		.remindMe(entity.getRemindMe())
                .status(entity.getStatus().name())
        		.build();

    }

    public static Task toEntityModel(TaskDto transport, Task entity) {

        if (transport == null || entity == null) {
            return null;
        }

        Task task = new Task();
        task.setId(transport.getId());
        task.setTitle(transport.getTitle());
        task.setDescription(transport.getDescription());
        task.setDueDate(transport.getDueDate());
        task.setRemindMe(transport.getRemindMe());
        task.setStatus(TaskStatus.valueOf(transport.getStatus()));

        return task;
    }

}
