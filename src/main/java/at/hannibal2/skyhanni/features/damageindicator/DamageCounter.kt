package at.hannibal2.skyhanni.features.damageindicator

import java.util.*

class DamageCounter {

    var currentDamage = 0L
    var currentHealing = 0L
    var oldDamages = LinkedList<OldDamage>()
    var firstTick = 0L

}