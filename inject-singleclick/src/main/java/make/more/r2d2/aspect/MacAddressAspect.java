package make.more.r2d2.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public final class MacAddressAspect {
    public static final String TAG = "MacAddressAspect";

    /*** {@link make.more.r2d2.aspect.PhoneInfos#getMacAddr() */
    @Pointcut("execution(* make.more.r2d2.aspect.PhoneInfos.getMacAddr())")
    public final void point() {
    }

    @Around("point()")
    public final Object aroundJoinPoint(ProceedingJoinPoint joinPoint) {
        System.out.println(TAG + "2");

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println(TAG + "3");
        }
        return "null2";
    }
}
