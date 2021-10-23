package io.ib67.serverutil;

import io.ib67.util.bukkit.Log;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleManager {
    private static final ModuleHolder ALWAYS_FALSE = ModuleHolder.builder().enabled(false).module(null).build();
    private Map<String, ModuleHolder> modules;
    private List<String> enabledModules;

    public ModuleManager(List<String> enabledModules) {
        // Search for available modules.
        modules = ServiceLoader.load(IModule.class, ModuleManager.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .map(IModule::register)
                .collect(Collectors.toMap(IModule::name, k -> ModuleHolder.builder()
                        .enabled(false)
                        .module(k)
                        .build()));
        this.enabledModules = enabledModules;
    }

    // Cant put it into a constructor because it will cause NPE for modules which used getModuleManager.
    protected void loadModules() {
        for (Map.Entry<String, ModuleHolder> stringModuleHolderEntry : modules.entrySet()) {
            if (enabledModules.contains(stringModuleHolderEntry.getKey())) {
                registerModule(stringModuleHolderEntry.getValue().module);
            }
        }
    }

    public void reloadModule(String moduleName) {
        if (isModuleActive(moduleName)) {
            disableModule(moduleName);
            enableModule(moduleName);
        } else {
            enableModule(moduleName);
            disableModule(moduleName);
        }
    }

    public void registerModule(IModule<?> module) {
        registerModule(module.name(), module, enabledModules.contains(module.name()));
    }

    public void registerModule(String moduleName, IModule<?> module, boolean initialState) {
        Log.info("Detected Module: " + moduleName);
        module.register();
        modules.put(moduleName, ModuleHolder.builder().module(module).initialized(true).enabled(initialState).initialized(true).build());
        if (initialState) enableModule(moduleName);
    }

    public boolean isModuleActive(String name) {
        return modules.getOrDefault(name, ALWAYS_FALSE).enabled;
    }

    public void enableModule(String name) {
        Optional.ofNullable(modules.get(name)).ifPresent(e -> {
            e.enabled = true;
            e.module.enable();
        });
    }

    public void disableModule(String name) {
        Optional.ofNullable(modules.get(name)).ifPresent(e -> {
            e.enabled = false;
            e.module.disable();
        });
    }

    public Stream<ModuleHolder> getModules() {
        return modules.values().stream();
    }

    public boolean isModuleAvailable(String name) {
        return modules.containsKey(name);
    }

    public Optional<IModule> getModuleBy(String name) {
        var pair = modules.get(name);
        if (!pair.enabled) {
            return Optional.empty();
        } else {
            return Optional.of(pair.module);
        }
    }

    @Getter
    @Builder
    public static class ModuleHolder {
        private IModule module;
        private boolean enabled;
        private boolean initialized;
    }
}
