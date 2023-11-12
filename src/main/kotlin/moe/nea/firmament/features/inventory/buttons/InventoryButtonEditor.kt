package moe.nea.firmament.features.inventory.buttons

import io.github.moulberry.moulconfig.xml.Bind
import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import org.lwjgl.glfw.GLFW
import net.minecraft.client.gui.DrawContext
import moe.nea.firmament.util.MoulConfigUtils

class InventoryButtonEditor(
    val lastGuiRect: Rectangle,
) : FragmentGuiScreen() {
    class Editor {
        @field:Bind
        var command: String = ""

        @field:Bind
        var icon: String = ""
    }

    data class Button(
        val x: Int,
        val y: Int,
        val anchorRight: Boolean,
        val anchorBottom: Boolean,
        val icon: String?,
        val command: String?,
    ) {
        fun isValid() = !icon.isNullOrBlank() && !command.isNullOrBlank()

        fun getPosition(guiRect: Rectangle): Point {
            return Point(
                (if (anchorRight) guiRect.maxX else guiRect.minX) + x,
                (if (anchorBottom) guiRect.maxY else guiRect.minY) + y,
            )
        }

        fun getBounds(guiRect: Rectangle): Rectangle {
            return Rectangle(getPosition(guiRect), Dimension(18, 18))
        }
    }

    val buttons = mutableListOf<Button>()
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
            createPopup(MoulConfigUtils.loadGui("button_editor_fragment", Editor()), Point(mouseX, mouseY))
            return true
        }
        if (lastGuiRect.contains(mouseX, mouseY) || lastGuiRect.contains(Point(mouseX + 18, mouseY + 18))) return true
        val mx = mouseX.toInt()
        val my = mouseY.toInt()
        val anchorRight = mx > lastGuiRect.maxX
        val anchorBottom = my > lastGuiRect.maxY
        val offsetX = mx - if (anchorRight) lastGuiRect.maxX else lastGuiRect.minX
        val offsetY = my - if (anchorBottom) lastGuiRect.maxY else lastGuiRect.minY
        buttons.add(Button(offsetX, offsetY, anchorRight, anchorBottom, null, null))
        return true
    }

}
