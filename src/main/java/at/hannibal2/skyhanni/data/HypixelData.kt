package at.hannibal2.skyhanni.data

import at.hannibal2.skyhanni.events.IslandChangeEvent
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.events.ProfileJoinEvent
import at.hannibal2.skyhanni.utils.LorenzLogger
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import at.hannibal2.skyhanni.utils.TabListUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

class HypixelData {

    companion object {
        var hypixel = false
        var skyblock = false
        var skyBlockIsland: String = ""

        fun readSkyBlockArea(): String {
            for (line in ScoreboardData.sidebarLinesFormatted()) {
                if (line.startsWith(" §7⏣ ")) {
                    return line.substring(5).removeColor()
                }
            }

            return "invalid"
        }

    }

    var loggerIslandChange = LorenzLogger("debug/island_change")

    @SubscribeEvent
    fun onConnect(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        hypixel = Minecraft.getMinecraft().runCatching {
            !event.isLocal && (thePlayer?.clientBrand?.lowercase()?.contains("hypixel")
                ?: currentServerData?.serverIP?.lowercase()?.contains("hypixel") ?: false)
        }.onFailure { it.printStackTrace() }.getOrDefault(false)
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        skyblock = false
    }

    @SubscribeEvent
    fun onDisconnect(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        hypixel = false
        skyblock = false
    }

    @SubscribeEvent
    fun onStatusBar(event: LorenzChatEvent) {
        if (!hypixel) return

        val message = event.message.removeColor().lowercase()

        if (message.startsWith("your profile was changed to:")) {
            val stripped = message.replace("your profile was changed to:", "").replace("(co-op)", "").trim()
            ProfileJoinEvent(stripped).postAndCatch()
        }
        if (message.startsWith("you are playing on profile:")) {
            val stripped = message.replace("you are playing on profile:", "").replace("(co-op)", "").trim()
            ProfileJoinEvent(stripped).postAndCatch()

        }
    }

    var tick = 0

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!hypixel) return
        if (event.phase != TickEvent.Phase.START) return

        tick++

        if (tick % 5 != 0) return

        val newState = checkScoreboard()
        if (newState) {
            checkIsland()
        }

        if (newState == skyblock) return
        skyblock = newState
    }

    private fun checkIsland() {
        var newIsland = ""
        var guesting = false
        for (line in TabListUtils.getTabList()) {
            if (line.startsWith("§r§b§lArea: ")) {
                newIsland = line.split(": ")[1].removeColor()
            }
            if (line == "§r Status: §r§9Guest§r") {
                guesting = true
            }
        }
        if (guesting) {
            newIsland = "$newIsland guesting"
        }

        if (skyBlockIsland != newIsland) {
            IslandChangeEvent(newIsland, skyBlockIsland).postAndCatch()
            loggerIslandChange.log(newIsland)
            skyBlockIsland = newIsland
        }
    }

    private fun checkScoreboard(): Boolean {
        val minecraft = Minecraft.getMinecraft()
        val world = minecraft.theWorld ?: return false

        val sidebarObjective = world.scoreboard.getObjectiveInDisplaySlot(1) ?: return false

        val displayName = sidebarObjective.displayName

        return displayName.removeColor().contains("SKYBLOCK")

    }

}