package net.mythicisland.spark.minestom

import net.minestom.server.MinecraftServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import kotlin.io.path.listDirectoryEntries
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SparkMinestomTest {

    private val directory = Files.createTempDirectory("spark-minestom")
    private val server = MinecraftServer.init()
    private val spark = SparkMinestom.builder(directory)
        .permissionHandler { _, _ -> true }
        .enable()

    @BeforeAll
    fun start() {
        server.start("127.0.0.1", 0)
    }

    @AfterAll
    fun stop() {
        spark.disable()
        MinecraftServer.stopCleanly()
    }

    @Test
    fun tpsCommand() {
        val sender = execute("spark tps")
        assertTrue(sender.awaitMessage("TPS from last"))
    }

    @Test
    fun healthCommand() {
        val sender = execute("spark health show")
        assertTrue(sender.awaitMessage("Memory usage"))
    }

    @Test
    fun profilerSavesToFile() {
        assertTrue(execute("spark profiler start --thread *").awaitMessage("Profiler is now running"))
        assertTrue(execute("spark profiler stop --save-to-file").awaitMessage("Data has been written to"))
        assertTrue(directory.listDirectoryEntries("*.sparkprofile").isNotEmpty())
    }

    @Test
    fun api() {
        assertNotNull(spark.getApi().tps())
        assertNotNull(spark.getApi().mspt())
    }

    private fun execute(command: String): TestCommandSender {
        val sender = TestCommandSender()
        MinecraftServer.getCommandManager().execute(sender, command)
        return sender
    }
}
