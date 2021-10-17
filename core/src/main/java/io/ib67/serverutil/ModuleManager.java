package io.ib67.serverutil;

import io.ib67.util.Pair;
import io.ib67.util.bukkit.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleManager {
    private static final Pair<Boolean, IModule> ALWAYS_FALSE = Pair.of(false, null);
    private Map<String, Pair<Boolean, IModule>> modules;
    private List<String> enabledModules;

    public ModuleManager(List<String> enabledModules) {
        // Search for available modules.
        modules = ServiceLoader.load(IModule.class, ModuleManager.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .map(IModule::register)
                .collect(Collectors.toMap(IModule::name, k -> Pair.of(false, k)));
        this.enabledModules = enabledModules;
    }

    // Cant put it into a constructor because it will cause NPE for modules which used getModuleManager.
    protected void loadModules() {
        enabledModules.forEach(this::enableModule);
    }

    public void registerModule(IModule<?> module) {
        registerModule(module.name(), module, enabledModules.contains(module.name()));
    }

    public void registerModule(String moduleName, IModule<?> module, boolean initialState) {
        Log.info("Detected Module: " + moduleName);
        modules.put(moduleName, Pair.of(false, module));
        if (initialState) enableModule(moduleName);
    }

    public boolean isModuleActive(String name) {
        return modules.getOrDefault(name, ALWAYS_FALSE).key;
    }

    public void enableModule(String name) {
        Optional.ofNullable(modules.get(name)).ifPresent(e -> {
            e.key = true;
            e.value.enable();
        });
    }

    public void disableModule(String name) {
        Optional.ofNullable(modules.get(name)).ifPresent(e -> {
            e.key = false;
            e.value.disable();
        });
    }

    public Stream<Pair<Boolean, IModule>> getModules() {
        return modules.values().stream();
    }

    public boolean isModuleAvailable(String name) {
        return modules.containsKey(name);
    }

    public Optional<IModule> getModuleBy(String name) {
        var pair = modules.get(name);
        if (!pair.key) {
            return Optional.empty();
        } else {
            return Optional.of(pair.value);
        }
    }

}
