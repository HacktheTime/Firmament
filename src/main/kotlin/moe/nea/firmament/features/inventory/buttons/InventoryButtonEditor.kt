package moe.nea.firmament.features.inventory.buttons

import io.github.moulberry.moulconfig.common.IItemStack
import io.github.moulberry.moulconfig.xml.Bind
import io.github.notenoughupdates.moulconfig.platform.ModernItemStack
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import org.lwjgl.glfw.GLFW
import net.minecraft.client.gui.DrawContext
import moe.nea.firmament.util.FragmentGuiScreen
import moe.nea.firmament.util.MoulConfigUtils

class InventoryButtonEditor(
    val lastGuiRect: Rectangle,
) : FragmentGuiScreen() {
    class Editor(val originalButton: InventoryButton) {
        @field:Bind
        var command: String = originalButton.command ?: ""

        @field:Bind
        var icon: String = originalButton.icon ?: ""

        @Bind
        fun getItemIcon(): IItemStack {
            save()
            return ModernItemStack.of(InventoryButton.getItemForName(icon))
        }

        fun save() {
            originalButton.icon = icon
            originalButton.command = command
        }
    }

    val buttons: MutableList<InventoryButton> =
        InventoryButtons.DConfig.data.buttons.map { it.copy() }.toMutableList()

    override fun close() {
        InventoryButtons.DConfig.data.buttons = buttons
        InventoryButtons.DConfig.markDirty()
        super.close()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(lastGuiRect.minX, lastGuiRect.minY, lastGuiRect.maxX, lastGuiRect.maxY, -1)

        for (button in buttons) {
            val buttonPosition = button.getBounds(lastGuiRect)
            context.fill(
                buttonPosition.minX, buttonPosition.minY,
                buttonPosition.maxX, buttonPosition.maxY,
                0xFF00FF00.toInt()
            )
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close()
            return true
        }
        return false
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (super.mouseClicked(mouseX, mouseY, button)) return true
        val clickedButton = buttons.firstOrNull { it.getBounds(lastGuiRect).contains(Point(mouseX, mouseY)) }
        if (clickedButton != null) {
            createPopup(MoulConfigUtils.loadGui("button_editor_fragment", Editor(clickedButton)), Point(mouseX, mouseY))
            return true
        }
        if (lastGuiRect.contains(mouseX, mouseY) || lastGuiRect.contains(
                Point(
                    mouseX + InventoryButton.dimensions.width,
                    mouseY + InventoryButton.dimensions.height,
                )
            )
        ) return true
        val mx = mouseX.toInt()
        val my = mouseY.toInt()
        val anchorRight = mx > lastGuiRect.maxX
        val anchorBottom = my > lastGuiRect.maxY
        val offsetX = mx - if (anchorRight) lastGuiRect.maxX else lastGuiRect.minX
        val offsetY = my - if (anchorBottom) lastGuiRect.maxY else lastGuiRect.minY
        buttons.add(InventoryButton(offsetX, offsetY, anchorRight, anchorBottom, null, null))
        return true
    }

}
