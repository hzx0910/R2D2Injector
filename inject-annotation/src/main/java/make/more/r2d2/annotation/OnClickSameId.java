package make.more.r2d2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create by HeZX on 2019/02/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OnClickSameId {
    /**
     * R.id.class或者R.class都可以
     */
    Class value();
}
