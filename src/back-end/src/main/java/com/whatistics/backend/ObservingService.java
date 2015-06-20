package com.whatistics.backend;

/**
 * @author robert
 */
public interface ObservingService<ObservedType> extends Service {
    void update(ObservableService<ObservedType> service, ObservedType data);
}