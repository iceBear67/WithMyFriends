package io.ib67.serverutil;

public interface IModule {
    String name();

    String description();

    /**
     * @return itself
     * @implSpec This method should return itself.
     */
    IModule register();

    void enable();

    void disable();
}
