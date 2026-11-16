package net.mythicisland.spark.minestom

import me.lucko.spark.common.platform.PlatformInfo
import net.minestom.server.MinecraftServer
import net.minestom.server.ping.Status

internal object MinestomPlatformInfo : PlatformInfo {

    override fun getType(): PlatformInfo.Type = PlatformInfo.Type.SERVER

    override fun getName(): String = "Minestom"

    override fun getBrand(): String = MinecraftServer.getBrandName()

    override fun getVersion(): String = Status.VersionInfo.DEFAULT.name

    override fun getMinecraftVersion(): String = Status.VersionInfo.DEFAULT.name
}
