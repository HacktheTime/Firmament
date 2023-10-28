package moe.nea.firmament.features.inventory.buttons

import io.github.moulberry.moulconfig.xml.Bind
import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class InventoryButtonEditor(
    val lastGuiRect: Rectangle,
) : Screen(Text.literal("")) {
    class Editor {
        @field:Bind
        var
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
        super.render(context, mouseX, mouseY, delta)

        context.fill(lastGuiRect.minX, lastGuiRect.minY, lastGuiRect.maxX, lastGuiRect.maxY, -1)

        for (button in buttons) {
            val buttonPosition = button.getPosition(lastGuiRect)
            context.fill(
                buttonPosition.x, buttonPosition.y,
                buttonPosition.x + 18, buttonPosition.y + 18,
                0xFF00FF00.toInt()
            )
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (lastGuiRect.contains(mouseX, mouseY)) return true
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
