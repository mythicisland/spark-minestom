package net.mythicisland.spark.minestom

import me.lucko.spark.api.Spark
import me.lucko.spark.api.SparkProvider
import me.lucko.spark.common.SparkBuildInfo
import me.lucko.spark.common.SparkPlatform
import me.lucko.spark.common.SparkPlugin
import me.lucko.spark.common.monitor.ping.PlayerPingProvider
import me.lucko.spark.common.platform.PlatformInfo
import me.lucko.spark.common.platform.world.WorldInfoProvider
import me.lucko.spark.common.sampler.ThreadDumper
import me.lucko.spark.common.tick.TickHook
import me.lucko.spark.common.tick.TickReporter
import me.lucko.spark.common.util.SparkThreadFactory
import me.lucko.spark.common.util.classfinder.ClassFinder
import me.lucko.spark.common.util.classfinder.FallbackClassFinder
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.function.BiPredicate
import java.util.logging.Level
import java.util.stream.Stream

internal class MinestomSparkPlugin(
    private val directory: Path,
    private val commands: Boolean,
    private val permissionHandler: BiPredicate<CommandSender, String>,
) : SparkPlugin, SparkMinestom {

    private val logger = LoggerFactory.getLogger(MinestomSparkPlugin::class.java)
    private val executor = Executors.newFixedThreadPool(ASYNC_THREADS, SparkThreadFactory())
    private val threadDumper = ThreadDumper.Regex(setOf("${MinecraftServer.THREAD_NAME_TICK}.*"))
    private val platform = SparkPlatform(this)
    private val command = MinestomSparkCommand.create(platform, permissionHandler)

    fun enable() {
        platform.enable()
        if (commands) {
            MinecraftServer.getCommandManager().register(command)
        }
    }

    override fun getApi(): Spark = SparkProvider.get()

    override fun disable() {
        if (commands) {
            MinecraftServer.getCommandManager().unregister(command)
        }
        platform.disable()
        executor.shutdown()
    }

    override fun getVersion(): String = SparkBuildInfo.VERSION

    override fun getPluginDirectory(): Path = directory

    override fun getCommandName(): String = "spark"

    override fun getCommandSenders(): Stream<MinestomCommandSender> =
        Stream.concat<CommandSender>(
            MinecraftServer.getConnectionManager().onlinePlayers.stream(),
            Stream.of(MinecraftServer.getCommandManager().consoleSender),
        ).map { MinestomCommandSender(it, permissionHandler) }

    override fun executeAsync(task: Runnable) {
        executor.execute(task)
    }

    override fun executeSync(task: Runnable) {
        MinecraftServer.getSchedulerManager().scheduleNextTick(task)
    }

    override fun log(level: Level, msg: String) {
        log(level, msg, null)
    }

    override fun log(level: Level, msg: String, throwable: Throwable?) {
        when {
            level.intValue() >= Level.SEVERE.intValue() -> logger.error(msg, throwable)
            level.intValue() >= Level.WARNING.intValue() -> logger.warn(msg, throwable)
            else -> logger.info(msg, throwable)
        }
    }

    override fun getDefaultThreadDumper(): ThreadDumper = threadDumper

    override fun createClassFinder(): ClassFinder = FallbackClassFinder.INSTANCE

    override fun createTickHook(): TickHook = MinestomTickHook()

    override fun createTickReporter(): TickReporter = MinestomTickReporter()

    override fun createPlayerPingProvider(): PlayerPingProvider = MinestomPlayerPingProvider

    override fun createWorldInfoProvider(): WorldInfoProvider = MinestomWorldInfoProvider

    override fun getPlatformInfo(): PlatformInfo = MinestomPlatformInfo

    companion object {
        private const val ASYNC_THREADS = 4
    }
}
