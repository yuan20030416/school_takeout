package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);


    /*
    * 根据菜品ID查出对应的套餐ID，
    * */
    List<Long> getStemealIdByDishId(List<Long> dishIds);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    Page pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
