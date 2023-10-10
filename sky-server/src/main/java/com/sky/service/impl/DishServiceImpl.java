package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;


    @Override
    public void add(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /*根据菜的Id查询菜品和口味*/
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /*修改菜品*/
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

    }

    @Override
    public DishVO getByCategoryId(Long categoryId) {
        DishVO dishVO = dishMapper.getByCategoryId(categoryId);
        return dishVO;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
        if (status == StatusConstant.DISABLE) {
            //如果是停售，还要把菜品所关联的套餐给停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            //所关联的套餐ID
            //List<Long> setmeals = setmealMapper.getStemealIdByDishId(dishIds);
            List<Long> setmeals = setmealDishMapper.getSetmealByDishId(dishIds);
            if (setmeals != null && setmeals.size() > 0)
                for (Long setmealId : setmeals) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(status)
                            .build();
                    setmealMapper.update(setmeal);
                }
        }
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //判断所含菜品中是否含有起售中的菜品
        for (Long id : ids) {
            Integer status = dishMapper.getById(id).getStatus();
            if(status == StatusConstant.ENABLE){
                //如果有 就不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能删除，如果包含在套餐中将不能被删除
        List<Long> setmealIds = setmealDishMapper.getSetmealByDishId(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //如果能删除，删除菜品 同时删除口味
        for (Long id : ids) {
            dishMapper.deletByIds(id);
            dishFlavorMapper.deleteFlavor(id);
        }
    }


}
