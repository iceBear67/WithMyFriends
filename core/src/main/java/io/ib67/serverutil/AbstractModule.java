package io.ib67.serverutil;

import io.ib67.util.Lazy;

public abstract class AbstractModule<T extends AbstractModuleConfig> implements IModule<T> {
    private final Lazy<Class<T>, T> configSupplier = Lazy.by(t -> WithMyFriends.getInstance().getModuleConfig().getConfig(this.name(), t));

    public void saveConfig(T config) {
        WithMyFriends.getInstance().getModuleConfig().saveConfig(this.name(), config);
        //WithMyFriends.getInstance().getModuleConfig().getModules().put(name(), config);
    }

    @SuppressWarnings("unchecked")
    public T getConfig(Class<T> configClass) {
        return configSupplier.get(configClass);
    }
}
