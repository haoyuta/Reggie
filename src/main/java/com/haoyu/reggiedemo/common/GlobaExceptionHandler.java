package com.haoyu.reggiedemo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
//该注解捕获指定Controller异常
@ControllerAdvice(annotations = {RestController.class,ControllerAdvice.class})
@ResponseBody
@Slf4j
public class GlobaExceptionHandler {

    /**
     * 处理账号重复异常方法
     * @param ex
     * @return
     */
    //该注解指定方法处理哪种异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            //注意split字符串方法，通过指定分隔符对字符串进行切片，这里指定为空格
            String[] split=ex.getMessage().split(" ");
            String msg=split[2]+"账号已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 异常处理方法，处理分类关联业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
