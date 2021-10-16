package io.ib67.serverutil;

public interface IModule<T extends AbstractModuleConfig> {
    String name();

    String description();

    /**
     * @return itself
     * @implSpec This method should return itself.
     */
    IModule register();

    void enable();

    void disable();

    default void saveConfig(T config) {
        WithMyFriends.getInstance().getModuleConfig().getModules().put(name(), config);
    }

    @SuppressWarnings("unchecked")
    default T getConfig() {
        return (T) WithMyFriends.getInstance().getModuleConfig().getModules().get(name());
    }
}
