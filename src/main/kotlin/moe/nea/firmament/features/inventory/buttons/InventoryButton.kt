package moe.nea.firmament.features.inventory.buttons

import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import kotlinx.serialization.Serializable
import net.minecraft.item.ItemStack
import moe.nea.firmament.repo.ItemCache.asItemStack
import moe.nea.firmament.repo.RepoManager
import moe.nea.firmament.util.SkyblockId

@Serializable
data class InventoryButton(
    val x: Int,
    val y: Int,
    val anchorRight: Boolean,
    val anchorBottom: Boolean,
    var icon: String?,
    var command: String?,
) {
    companion object {
        val dimensions = Dimension(18, 18)
        fun getItemForName(icon: String): ItemStack {
            return RepoManager.getNEUItem(SkyblockId(icon)).asItemStack(idHint = SkyblockId(icon))
        }
    }

    fun isValid() = !icon.isNullOrBlank() && !command.isNullOrBlank()

    fun getPosition(guiRect: Rectangle): Point {
        return Point(
            (if (anchorRight) guiRect.maxX else guiRect.minX) + x,
            (if (anchorBottom) guiRect.maxY else guiRect.minY) + y,
        )
    }

    fun getBounds(guiRect: Rectangle): Rectangle {
        return Rectangle(getPosition(guiRect), dimensions)
    }

    fun getItem(): ItemStack {
        return getItemForName(icon ?: "")
    }

}
