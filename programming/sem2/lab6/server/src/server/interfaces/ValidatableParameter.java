package server.interfaces;

public interface ValidatableParameter<T> {
    T validate(Object parameter);
}
