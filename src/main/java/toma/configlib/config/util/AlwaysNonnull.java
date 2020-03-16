package toma.configlib.config.util;

import java.util.function.Supplier;

public class AlwaysNonnull<T> {

    private final Supplier<T> supplier;
    private final T ifn;

    public AlwaysNonnull(Supplier<T> getter, T ifNull) {
        this.supplier = getter;
        this.ifn = ifNull;
        if(ifNull == null)
            throw new NullPointerException();
    }

    public T get() {
        T t = supplier.get();
        return t != null ? t : ifn;
    }
}
