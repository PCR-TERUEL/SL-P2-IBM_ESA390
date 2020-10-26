package com.legados.wrapperIbm.controller;


public interface WindowObserver {
    enum Event { NEW }

    void eventHappened(Event event, Object obj);
}
