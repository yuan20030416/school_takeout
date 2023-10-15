package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void add(DishDTO dishDTO);


    PageResult pageQuery(DishPageQueryDTO dto);

    DishVO getByIdWithFlavor(Long id);

    void update(DishDTO dishDTO);



    void startOrStop(Integer status,Long id);

    void deleteByIds(List<Long> ids);

    List<Dish> getBycategoryId(Long categoryId);
}
