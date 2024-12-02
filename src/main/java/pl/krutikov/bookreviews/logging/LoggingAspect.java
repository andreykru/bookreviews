package pl.krutikov.bookreviews.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(Logging)")
    public Object methodTimeAndExceptionLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Entering method: {}.{}()", className, methodName);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(methodName);

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            log.info("Exiting method: {}.{}(), execution time: {}", className, methodName, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.info("Exiting method: {}.{}(), execution time: {}, exception: {}", className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage());
            throw e;
        }
    }

}
