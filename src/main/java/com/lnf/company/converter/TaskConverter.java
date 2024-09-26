/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.converter;

import com.lnf.company.model.Task;
import com.lnf.company.model.enums.TaskStatus;
import com.lnf.dto.company.TaskDto;

public class TaskConverter {

    private TaskConverter() {
    }

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
