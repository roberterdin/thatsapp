package com.whatistics.backend;

import java.util.LinkedList;
import java.util.List;

/**
 * Service that can be observed by a class implementing the {@link ObservingService ObservingService} interface.
 * @author robert
 */
public abstract class ObservableService<ObservedType> implements Service {

    private List<ObservingService<ObservedType>> _observers =
            new LinkedList<>();

    public void addObserver(ObservingService<ObservedType> obs) {
        if (obs == null) {
            throw new IllegalArgumentException("Tried to add a null observer");
        }
        if (_observers.contains(obs)) {
            return;
        }
        _observers.add(obs);
    }

    public void notifyObservers(ObservedType data) {
        for (ObservingService<ObservedType> obs : _observers) {
            obs.update(this, data);
        }
    }

    @Override
    public abstract void start();

    @Override
    public abstract void stop();
}
