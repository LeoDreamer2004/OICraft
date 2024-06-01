package org.dindier.oicraft.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.assets.exception.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UserNotLoggedInException.class)
    public RedirectView UserNotFoundHandler() {
        return new RedirectView("/login");
    }

    @ExceptionHandler(NoAuthenticationError.class)
    public ModelAndView NoAuthenticationHandler(NoAuthenticationError e) {
        return new ModelAndView("/error/403")
                .addObject("error", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView EntityNotFoundHandler(EntityNotFoundException e) {
        return new ModelAndView("/error/404")
                .addObject("error", e.getMessage());
    }

    @ExceptionHandler(AdminOperationError.class)
    public ModelAndView adminOperationHandler(AdminOperationError e) {
        return new ModelAndView("/admin/error")
                .addObject("error", e.getMessage());
    }

    @ExceptionHandler(CodeCheckerError.class)
    public void CodeCheckerHandler(CodeCheckerError e) {
        log.error("Code checker error: {}", Arrays.toString(e.getStackTrace()));
    }
}
