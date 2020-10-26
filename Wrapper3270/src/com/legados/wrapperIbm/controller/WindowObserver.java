package com.legados.wrapperIbm.controller;


public interface WindowObserver {

    enum Event { NEW, CLOSE;
    }

    void eventHappened(Event event, Object obj);
}
