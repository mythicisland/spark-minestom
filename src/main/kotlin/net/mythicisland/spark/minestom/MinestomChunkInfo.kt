package net.mythicisland.spark.minestom

import me.lucko.spark.common.platform.world.AbstractChunkInfo
import me.lucko.spark.common.platform.world.CountMap
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.instance.Chunk

internal class MinestomChunkInfo(
    chunk: Chunk,
    entities: Collection<Entity>,
) : AbstractChunkInfo<EntityType>(chunk.chunkX, chunk.chunkZ) {

    private val entityTypes = entities.map { it.entityType }

    override fun getEntityCounts(): CountMap<EntityType> {
        val counts = CountMap.Simple<EntityType>(HashMap())
        entityTypes.forEach { counts.increment(it) }
        return counts
    }

    override fun entityTypeName(type: EntityType): String = type.key().value()
}
