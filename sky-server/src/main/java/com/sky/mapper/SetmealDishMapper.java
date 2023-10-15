package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealByDishId(List<Long> ids);

    void saveDishes(List<SetmealDish> setmealDishes);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteById(Long setmealId);

    void update(SetmealDish setmealDish);

    @Select("select dish_id from setmeal_dish where setmeal_id = #{id}")
    List<Long> getDishesBySetmealId(Long id);
}
