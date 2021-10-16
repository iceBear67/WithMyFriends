package io.ib67.serverutil;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ModuleConfig {
    private Map<String, AbstractModuleConfig> modules = new HashMap<>();
}
