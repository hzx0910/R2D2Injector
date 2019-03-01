package make.more.r2d2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Calendar;

@Aspect
public final class SingleClickAspect {
    public static final String TAG = "SingleClickAspect";
    public static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    @Pointcut("execution(@make.more.r2d2.annotation.OnClickSameId * *(..))")
    public final void methodAnnotated() {
    }

    @Around("methodAnnotated()")
    public final void aroundJoinPoint(ProceedingJoinPoint joinPoint) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            System.out.println("SingleClickAspect!");
        }
    }
}
