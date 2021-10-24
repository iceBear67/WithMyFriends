package io.ib67.serverutil;

import io.ib67.serverutil.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.Set;

/**
 * WithMyFriends 明确暴露的 API。
 */
public interface WithMyFriendsAPI {

    /**
     * 注册一个根命令，随之可以使用/%command% %args%。
     * 会覆盖掉原版的指令，选择需谨慎
     *
     * @param module  所属模块
     * @param command 指令
     * @param holder  指令信息，使用 {@link CommandHolder#builder()} 开始构建一个 CommandHolder.
     */
    public void registerRootCommand(IModule module, String command, CommandHolder holder);

    /**
     * 注册一个命令，随之可以使用 /util %command% %args% 的形式使用。
     *
     * @param module  所属模块
     * @param command 根命令
     * @param holder  指令信息，使用 {@link CommandHolder#builder()} 开始构建一个 CommandHolder.
     */
    public void registerCommand(IModule module, String command, CommandHolder holder);

    /**
     * 获取模块配置
     *
     * @return 模块配置
     */
    public ConfigManager<AbstractModuleConfig> getModuleConfig();

    /**
     * 获取命令信息
     *
     * @param command 根命令
     * @return 信息，可能为空值({@link Optional#empty()})
     */
    public Optional<CommandHolder> getCommandHolder(String command); //todo 可以通过一个注册类型来解决方法分裂问题。

    /**
     * 获取通过根命令注册方法的命令信息
     *
     * @param command 根命令
     * @return 信息，可能为空值({@link Optional#empty()})
     */
    public Optional<CommandHolder> getRootCommandHolder(String command);

    /**
     * 获取被注册过的模块命令
     *
     * @return 被注册过的模块命令，不为 Null
     */
    public Set<String> registeredModuleCommands();

    /**
     * 获取模块管理器
     *
     * @return 模块管理器
     */
    ModuleManager getModuleManager();

    /**
     * 返回一个 JavaPlugin 视图
     *
     * @return View of JavaPlugin.
     */
    default JavaPlugin asPlugin() {
        return (WithMyFriends) this;
    }
}
