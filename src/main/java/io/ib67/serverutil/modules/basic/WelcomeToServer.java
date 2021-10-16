package io.ib67.serverutil.modules.basic;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.IModule;

@AutoService(IModule.class)
public class WelcomeToServer implements IModule {
    @Override
    public String name() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public IModule register() {
        return null;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}
