package com.salami.dragon.engine;

public interface IApplication {
    // Dragon API methods to control window stuff.
    public int WIDTH();
    public int HEIGHT();
    public String TITLE();

    public void init();
    public void tick(float delta);
}
