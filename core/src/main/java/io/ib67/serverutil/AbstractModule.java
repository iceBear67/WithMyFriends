package io.ib67.serverutil;

import io.ib67.util.Lazy;
import net.jodah.typetools.TypeResolver;

public abstract class AbstractModule<T extends AbstractModuleConfig> implements IModule<T> {
    private final Lazy<?, T> configSupplier = Lazy.by(this::lazyGetConfig);

    @SuppressWarnings("unchecked")
    private T lazyGetConfig(Object ignored) {
        var solveResult = TypeResolver.resolveRawArguments(AbstractModule.class, this.getClass());
        if (solveResult.length != 1) {
            throw new UnsupportedOperationException("This class didn't declare which Config class it uses.");
        }
        var config = WithMyFriends.getInstance().getModuleConfig().getConfig(this.name(), (Class<? extends AbstractModuleConfig>) solveResult[0]);
        return (T) config;
    }

    public void saveConfig(T config) {
        WithMyFriends.getInstance().getModuleConfig().saveConfig(this.name(), config);
        //WithMyFriends.getInstance().getModuleConfig().getModules().put(name(), config);
    }

    @SuppressWarnings("unchecked")
    public T getConfig() {
        return configSupplier.get();
    }
}