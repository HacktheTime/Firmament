package moe.nea.firmament.features.inventory.buttons

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import moe.nea.firmament.events.HandledScreenClickEvent
import moe.nea.firmament.events.HandledScreenForegroundEvent
import moe.nea.firmament.events.HandledScreenPushREIEvent
import moe.nea.firmament.features.FirmamentFeature
import moe.nea.firmament.features.debug.DeveloperFeatures.getRectangle
import moe.nea.firmament.gui.config.ManagedConfig
import moe.nea.firmament.mixins.accessor.AccessorHandledScreen
import moe.nea.firmament.util.MC
import moe.nea.firmament.util.data.DataHolder

object InventoryButtons : FirmamentFeature {
    override val identifier: String
        get() = "inventory-buttons"

    object TConfig : ManagedConfig(identifier) {}
    object DConfig : DataHolder<Data>(serializer(), identifier, ::Data)
    @Serializable
    data class Data(
        var buttons: MutableList<InventoryButton> = mutableListOf()
    )


    override val config: ManagedConfig
        get() = TConfig

    fun getValidButtons() = DConfig.data.buttons.asSequence().filter { it.isValid() }
    override fun onLoad() {
        HandledScreenForegroundEvent.subscribe {
            it.screen as AccessorHandledScreen
            val bounds = it.screen.getRectangle()
            for (button in getValidButtons()) {
                val buttonBounds = button.getBounds(bounds)
                // TODO: render background
                it.context.drawItem(button.getItem(), buttonBounds.minX + 1, buttonBounds.minY + 1)
            }
        }
        HandledScreenClickEvent.subscribe {
            it.screen as AccessorHandledScreen
            val bounds = it.screen.getRectangle()
            for (button in getValidButtons()) {
                val buttonBounds = button.getBounds(bounds)
                if (buttonBounds.contains(it.mouseX, it.mouseY)) {
                    MC.sendCommand(button.command!! /* non null invariant covered by getValidButtons */)
                    break
                }
            }
        }
        HandledScreenPushREIEvent.subscribe {
            it.screen as AccessorHandledScreen
            val bounds = it.screen.getRectangle()
            for (button in getValidButtons()) {
                val buttonBounds = button.getBounds(bounds)
                it.block(buttonBounds)
            }
        }
    }
}
