package at.hannibal2.skyhanni.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot

object InventoryUtils {

    //TODO use this method more widely
    fun currentlyOpenInventory(): String {
        val screen = Minecraft.getMinecraft().currentScreen
        if (screen !is GuiChest) return ""
        val chest = screen.inventorySlots as ContainerChest

        return chest.lowerChestInventory.displayName.unformattedText.trim()
    }

    fun getItemsInOpenChest(): List<Slot> {
        val list = mutableListOf<Slot>()
        val guiChest = Minecraft.getMinecraft().currentScreen as GuiChest
        val inventorySlots = guiChest.inventorySlots.inventorySlots
        val skipAt = inventorySlots.size - 9 * 4
        var i = 0
        for (slot in inventorySlots) {
            val stack = slot.stack
            if (stack != null) {
                list.add(slot)
            }
            i++
            if (i == skipAt) break
        }
        return list
    }
}