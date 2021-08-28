package com.salami.dragon.engine.event;

import com.salami.dragon.engine.log.Logger;

public interface IListener {
    public void onEngineEvent(Event event);
    public void onKeyOrMouseButtonEvent(Event event, int key);
    public void onMouseMoveEvent(Event event, double xPos, double yPos);
    public void onMouseScrollEvent(Event event, double amount);
}
