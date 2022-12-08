package at.hannibal2.skyhanni.features.bazaar

import at.hannibal2.skyhanni.utils.ItemUtils.getInternalName
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import net.minecraft.item.ItemStack

class BazaarApi {

    companion object {
        private val bazaarMap = mutableMapOf<String, BazaarData>()

        fun isBazaarInventory(inventoryName: String): Boolean {
            if (inventoryName.contains(" ➜ ") && !inventoryName.contains("Museum")) return true
            if (BazaarOrderHelper.isBazaarOrderInventory(inventoryName)) return true

            return when (inventoryName) {
                "Your Bazaar Orders" -> true
                "How many do you want?" -> true
                "How much do you want to pay?" -> true
                "Confirm Buy Order" -> true
                "Confirm Instant Buy" -> true
                "At what price are you selling?" -> true
                "Confirm Sell Offer" -> true
                "Order options" -> true

                else -> false
            }
        }

        fun getCleanBazaarName(name: String): String {
            if (name.endsWith(" Gemstone")) {
                return name.substring(6)
            }
            return name.removeColor()
        }

        fun getBazaarDataForName(name: String): BazaarData? {
            if (bazaarMap.containsKey(name)) {
                val bazaarData = bazaarMap[name]
                if (bazaarData != null) {
                    return bazaarData
                }
                LorenzUtils.error("Bazaar data not found! '$name'")
            }
            return null
        }

        fun isBazaarItem(stack: ItemStack): Boolean {
            val internalName = stack.getInternalName()
            return bazaarMap.any { it.value.apiName == internalName }

        }
    }

    init {
        BazaarDataGrabber(bazaarMap).start()
    }
}