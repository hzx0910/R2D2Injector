package make.more.r2d2.annotation;

/**
 * create by HeZX on 2019/02/18
 */
public interface Injector<T> {
    void inject(T target);

    void inject(T target, Object source);
}
