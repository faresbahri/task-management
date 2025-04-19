package com.taskmanagement.task.mapper;


import org.springframework.data.domain.Page;

import java.util.List;

public interface BaseMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);


    List<D> toDtoList(List<E> entities);

    List<E> toEntity(List<D> dtos);

    Page<D> toDtoPage(Page<E> pageEntity);
}
