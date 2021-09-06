package com.salami.dragon.engine.event;

public interface IListener {
    public void onEngineEvent(Event event);
    public void onAudioEvent(Event event);
    public void onComponentEvent(Event event);
    public void onKeyOrMouseButtonEvent(Event event, int key);
    public void onMouseMoveEvent(Event event, double xPos, double yPos);
    public void onMouseScrollEvent(Event event, double amount);
}
