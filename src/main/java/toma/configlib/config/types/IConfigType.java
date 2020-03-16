package toma.configlib.config.types;

public interface IConfigType<T> {

    String getKey();

    T get();
}
