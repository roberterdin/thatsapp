package com.whatistics.backend;

/**
 * Observing counterpart for {@link ObservableService}
 * @author robert
 */
public interface ObservingService<ObservedType> extends Service {
    void update(ObservableService<ObservedType> service, ObservedType data);
}