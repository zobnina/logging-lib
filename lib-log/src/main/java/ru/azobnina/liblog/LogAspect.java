package ru.azobnina.liblog;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("within(@ru.azobnina.liblog.InfoLog *)")
    public void infoLogClass() {
        //PointCut
    }

    @Pointcut("within(@ru.azobnina.liblog.DebugLog *)")
    public void debugLogClass() {
        //PointCut
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
        //PointCut
    }

    @Pointcut("publicMethod() && infoLogClass()")
    public void infoLogMethods() {
        //PointCut
    }

    @Before("infoLogMethods()")
    public void logInfoStart(JoinPoint jp) {

        log.info("{}: {}(): start. {}", className(jp), methodName(jp), argMsg(jp));
    }

    @AfterReturning(pointcut = "infoLogMethods()", returning = "retVal")
    public void logInfoReturn(JoinPoint jp, Object retVal) {

        log.info("{}: {}(): end. {}", className(jp), methodName(jp), returnMsg(retVal));
    }

    @Before("debugLogClass()")
    public void logDebugStart(JoinPoint jp) {

        if (log.isDebugEnabled()) {
            log.debug("{}: {}(): start. {}", className(jp), methodName(jp), argMsg(jp));
        }
    }

    @AfterReturning(pointcut = "debugLogClass()", returning = "retVal")
    public void logDebugReturn(JoinPoint jp, Object retVal) {

        if (log.isDebugEnabled()) {
            log.debug("{}: {}(): end. {}", className(jp), methodName(jp), returnMsg(retVal));
        }
    }

    private String argMsg(JoinPoint jp) {

        var logBuilder = new StringBuilder();
        if (jp.getArgs() != null && jp.getArgs().length != 0) {
            logBuilder.append("\nArgs: ");
            var codeSignature = (CodeSignature) jp.getSignature();
            for (var i = 0; i < jp.getArgs().length; i++) {
                logBuilder.append(codeSignature.getParameterNames()[i]);
                logBuilder.append(" = ");
                logBuilder.append(jp.getArgs()[i]);
                if (jp.getArgs()[i] != jp.getArgs()[jp.getArgs().length - 1]) {
                    logBuilder.append(", ");
                }
            }
        }

        return logBuilder.toString();
    }

    private String returnMsg(Object retVal) {

        var logBuilder = new StringBuilder();
        if (retVal != null) {
            logBuilder.append("\nReturn = ");
            logBuilder.append(retVal);
        }

        return logBuilder.toString();
    }

    private String className(JoinPoint jp) {

        return jp.getTarget().getClass().getSimpleName();
    }

    private String methodName(JoinPoint jp) {

        return jp.getSignature().getName();
    }
}
