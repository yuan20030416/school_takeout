package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

public interface DishService {
    void add(DishDTO dishDTO);


    PageResult pageQuery(DishPageQueryDTO dto);

    DishVO getByIdWithFlavor(Long id);
}
