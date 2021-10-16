package io.ib67.serverutil;

import lombok.Getter;

import java.util.List;

@Getter
public class Config {
    private boolean updateCheck = true;
    private List<String> enabledModules = List.of("manager");
}
