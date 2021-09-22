package com.example.inmemoryweb.Exceptions;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Controller
@ControllerAdvice
public class TableExceptionHandler {
    @ExceptionHandler(value = {TableException.class})
    public ModelAndView handleTableException(TableException exception) {
        ModelAndView modelAndView = new ModelAndView("tableData");
        modelAndView.addObject("errorMessage", exception.getMessage());
        return modelAndView;
    }
}
