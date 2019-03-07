package make.more.r2d2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.HashMap;

@Aspect
public final class SingleClickAspect {
    public static final String TAG = "SingleClickAspect";
    public static final int MIN_CLICK_DELAY_TIME = 500;
    //针对不同方法分辨记录最近点击时间，避免同一个注解下方法无法相互调用
    private HashMap<String, Long> map = new HashMap<>();

    @Pointcut("execution(@make.more.r2d2.annotation.OnClickSameId * *(..))")
    public final void methodAnnotated() {
    }

    @Around("methodAnnotated()")
    public final void aroundJoinPoint(ProceedingJoinPoint joinPoint) {
        String key = joinPoint.toLongString();
        long lastClickTime = map.containsKey(key) ? map.get(key) : 0;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            System.out.println("SingleClickAspect!");
        }
        map.put(key, System.currentTimeMillis());
    }
}
