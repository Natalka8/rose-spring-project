// src/main/java/org/example/mapper/TaskMapper.java
package org.example.mapper;

import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
import org.example.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskRequest taskRequest);

    TaskResponse toDto(Task task);

    List<TaskResponse> toDtoList(List<Task> tasks);
}