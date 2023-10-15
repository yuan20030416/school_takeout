package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //在套餐表保存数据
        setmealMapper.insert(setmeal);

        //获取生成的套餐ID
        Long setmealId = setmeal.getId();

        //获取套餐里面的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        //保存关系
        setmealDishMapper.saveDishes(setmealDishes);


    }

    @Override
    public void delete(List<Long> setmealIds) {
        //起售中的套餐不能删除
        for (Long setmealId : setmealIds) {
            int status = setmealMapper.getStatus(setmealId);
            if(status == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //删除套餐的同时删除套餐与菜品的关系
        for (Long setmealId : setmealIds) {
            setmealMapper.deleteById(setmealId);
            setmealDishMapper.deleteById(setmealId);
        }

    }


    @Override
    public SetmealVO selectById(Long id) {
        return setmealMapper.selectById(id);
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //修改套餐表（setmeal）
        setmealMapper.update(setmeal);

        //获取套餐ID
        Long setmealId = setmeal.getId();

        //删除套餐菜品关系表中原来的套餐菜品关系
        setmealDishMapper.deleteById(setmealId);


        //重新插入新的套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.saveDishes(setmealDishes);
    }

    @Override
    public void startOrStop(Integer status,Long id) {
        if (status == StatusConstant.ENABLE) {
            //起售套餐时，如果套餐内包含停售的菜品，则不能起售
            List<Long> dishIds = setmealDishMapper.getDishesBySetmealId(id);
            for (Long dishId : dishIds) {
                int dishStatus = dishMapper.getStatus(dishId);
                if(dishStatus == StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setmealMapper.update(setmeal);

    }
}
