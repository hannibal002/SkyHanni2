package at.hannibal2.skyhanni.data

import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import net.minecraft.client.Minecraft
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class ScoreboardData {

    companion object {
        private val splitIcons = listOf(
            "\uD83C\uDF6B",
            "\uD83D\uDCA3",
            "\uD83D\uDC7D",
            "\uD83D\uDD2E",
            "\uD83D\uDC0D",
            "\uD83D\uDC7E",
            "\uD83C\uDF20",
            "\uD83C\uDF6D",
            "⚽",
            "\uD83C\uDFC0",
            "\uD83D\uDC79",
            "\uD83C\uDF81",
            "\uD83C\uDF89",
            "\uD83C\uDF82",
        )

        fun sidebarLinesFormatted(): List<String> {
            val list = mutableListOf<String>()
            for (line in sidebarLinesRaw) {
                val seperator = splitIcons.find { line.contains(it) } ?: continue
                val split = line.split(seperator)
                val start = split[0]
                var end = split[1]
                if (end.length >= 2) {
                    end = end.substring(2)
                }

                list.add(start + end)

            }

            return list
        }

        var sidebarLines: List<String> = emptyList()
        var sidebarLinesRaw: List<String> = emptyList()
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        val list = fetchScoreboardLines()
        sidebarLines = list.map { cleanSB(it) }.reversed()
        sidebarLinesRaw = list.reversed()
    }

    private fun cleanSB(scoreboard: String): String {
        return scoreboard.removeColor().toCharArray().filter { it.code in 21..126 }.joinToString(separator = "")
    }

    fun fetchScoreboardLines(): List<String> {
        val scoreboard = Minecraft.getMinecraft().theWorld?.scoreboard ?: return emptyList()
        val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return emptyList()
        var scores = scoreboard.getSortedScores(objective)
        val list = scores.filter { input: Score? ->
            input != null && input.playerName != null && !input.playerName.startsWith("#")
        }
        scores = if (list.size > 15) {
            list.drop(15)
        } else {
            list
        }
        return scores.map {
            ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(it.playerName), it.playerName)
        }
    }
}