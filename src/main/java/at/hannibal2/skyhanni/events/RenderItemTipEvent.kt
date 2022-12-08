package at.hannibal2.skyhanni.events

import net.minecraft.item.ItemStack

class RenderItemTipEvent(
    val stack: ItemStack,
    var stackTip: String = "",
) : LorenzEvent()