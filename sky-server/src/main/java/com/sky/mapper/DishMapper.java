package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @Insert("insert into dish (name, category_id, price, image, description, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name},#{categoryId},#{price},#{image},#{description},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Dish dish);


    Page<DishVO> pageQuery(DishPageQueryDTO dto);

    /*根据菜的Id查询菜品和口味*/
    DishVO getByIdWithFlavor(Long id);
}
