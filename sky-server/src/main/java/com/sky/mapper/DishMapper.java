package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into dish (name, category_id, price, image, description, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name},#{categoryId},#{price},#{image},#{description},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Dish dish);


    Page<DishVO> pageQuery(DishPageQueryDTO dto);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    @Delete("delete from dish where id = #{id}")
    void deletByIds(Long id);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> getBycategoryId(Long categoryId);

    @Select("select status from dish where id = #{id}")
    Integer getStatus(Long id);
}
