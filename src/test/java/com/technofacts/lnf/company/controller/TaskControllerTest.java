package com.technofacts.lnf.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technofacts.lnf.company.BaseTestClass;
import com.technofacts.lnf.company.service.TaskService;
import com.technofacts.lnf.dto.company.TaskDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest extends BaseTestClass  {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskService service;
    private UUID taskId;

    @BeforeAll
    void beforeAll() {
        taskId = UUID.fromString("1bb2af60-5565-41f8-b60a-6d139cba5178");
    }

    @BeforeEach
    void setUp() {
        // Common setup code if necessary
    }

    @Test
    void findById() throws Exception {

        UUID id = UUID.fromString("1bb2af60-5565-41f8-b60a-6d139cba5178");

        TaskDto expectedDto = mockTask1();

        given(service.findByTaskId(any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/task"  + "/" + id;

        performAndVerifyGet(url, status().isOk(), id.toString(), expectedDto);

        verify(service, times(1)).findByTaskId(any(UUID.class));
    }

    @Test
    void createTask() {

        // Arrange
        List<TaskDto> mockTasks = List.of(mockTask1(),mockTask2());
        doNothing().when(service).create(mockTasks);
        String url = "/lnf/company/tasks";

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TaskDto>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(mockTasks)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        verify(service).create(captor.capture());
        List<TaskDto> actualTasks = captor.getValue();

        // Check if the lists have the same size
        assertEquals(actualTasks.size(), actualTasks.size(), "The number of tasks created should match");

        // Check if the details of each Task match
        for (int i = 0; i < actualTasks.size(); i++) {
            assertEquals(actualTasks.get(i).getDescription(), actualTasks.get(i).getDescription(),
                    "Description should match for task at index " + i);
            assertEquals(actualTasks.get(i).getTitle(), actualTasks.get(i).getTitle(),
                    "Title should match for task at index " + i);
            assertEquals(actualTasks.get(i).getDueDate(), actualTasks.get(i).getDueDate(),
                    "DueDate should match for task at index " + i);
            assertEquals(actualTasks.get(i).getRemindMe(), actualTasks.get(i).getRemindMe(),
                    "RemindMe should match for task at index " + i);
            assertEquals(actualTasks.get(i).getStatus(), actualTasks.get(i).getStatus(),
                    "Status should match for task at index " + i);
        }
    }

    @Test
    void updateTask() {

        TaskDto updatedTask = mockTask2();
        updatedTask.setId(taskId);

        Mockito.doNothing().when(service).update(Mockito.eq(taskId), Mockito.any(TaskDto.class));
        String url = "/lnf/company/task/" + taskId;
        ArgumentCaptor<TaskDto> captor = ArgumentCaptor.forClass(TaskDto.class);

        // Act
        try {
            mockMvc.perform(put(url)
                            .content(asJsonString(updatedTask)) // Convert TaskDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        //verify the service
        verify(service).update(eq(taskId), any(TaskDto.class));

        // Assert
        Mockito.verify(service, times(1)).update(eq(taskId), captor.capture());
        TaskDto actualType = captor.getValue();

        assertEquals(updatedTask.getId(), actualType.getId(), "Task IDs should match");
        assertEquals(updatedTask.getStatus(), actualType.getStatus(), "Task status should match");
        assertEquals(updatedTask.getTitle(), actualType.getTitle(), "Task title should match");
        assertEquals(updatedTask.getRemindMe(), actualType.getRemindMe(), "Task remindMe should match");
        assertEquals(updatedTask.getDueDate(), actualType.getDueDate(), "Task dueDate should match");
        assertEquals(updatedTask.getDescription(), actualType.getDescription(), "Task description should match");
    }

    @Test
    void deleteTask() throws Exception {

        String url = "/lnf/company/task/" + taskId;
        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteById(taskId);
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     TaskDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(containsString)) // Additional, more specific validation
                .andExpect(jsonPath("$.title").value(expectedDto.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.dueDate").value(expectedDto.getDueDate().toString()))
                .andExpect(jsonPath("$.remindMe").value(expectedDto.getRemindMe().toString()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus()));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TaskDto mockTask1() {
        return createTask("1bb2af60-5565-41f8-b60a-6d139cba5178", "Profile has to be created" , "the profiles has to be added in the database",
                "TODO","2024-01-05", "2024-01-01");
    }

    private TaskDto mockTask2() {
        return createTask("cc24295f-c28f-4046-b614-8f9dc4665d62", "timesheet has to be created" , "the timesheet has to be added in the database",
                "COMPLETED","2024-01-10", "2024-01-05");
    }


    private TaskDto createTask(String id, String title, String description, String status, String dueDate, String remindMe) {
        TaskDto dto = new TaskDto();
        dto.setId(UUID.fromString(id));
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setDueDate(LocalDate.parse(dueDate));
        dto.setStatus(status);
        dto.setRemindMe(LocalDate.parse(remindMe));
        return dto;
    }

}
