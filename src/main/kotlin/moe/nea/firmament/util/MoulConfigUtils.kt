package moe.nea.firmament.util

import io.github.moulberry.moulconfig.common.MyResourceLocation
import io.github.moulberry.moulconfig.gui.GuiContext
import io.github.moulberry.moulconfig.xml.XMLUniverse

object MoulConfigUtils {
    val universe = XMLUniverse.getDefaultUniverse()
    fun loadGui(name: String, bindTo: Any): GuiContext {
        return GuiContext(universe.load(bindTo, MyResourceLocation("firmament", "gui/$name.xml")))
    }
}
