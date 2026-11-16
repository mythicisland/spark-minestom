package net.mythicisland.spark.minestom

import me.lucko.spark.common.monitor.ping.PlayerPingProvider
import net.minestom.server.MinecraftServer

internal object MinestomPlayerPingProvider : PlayerPingProvider {

    override fun poll(): Map<String, Int> =
        MinecraftServer.getConnectionManager().onlinePlayers.associate { it.username to it.latency }
}
