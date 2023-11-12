package moe.nea.firmament.util

import io.github.moulberry.moulconfig.gui.GuiContext
import io.github.moulberry.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper
import me.shedaniel.math.Point
import net.minecraft.client.gui.DrawContext
import moe.nea.firmament.util.MC

class MoulConfigFragment(
    context: GuiContext,
    val position: Point,
    val dismiss: () -> Unit
) : GuiComponentWrapper(context) {
    init {
        this.init(MC.instance, MC.screen!!.width, MC.screen!!.height)
    }

    override fun createContext(drawContext: DrawContext?): GuiImmediateContext {
        val oldContext = super.createContext(drawContext)
        return oldContext.translated(
            position.x,
            position.y,
            context.root.width,
            context.root.height,
        )
    }


    override fun render(drawContext: DrawContext?, i: Int, j: Int, f: Float) {
        val ctx = createContext(drawContext)
        val m = drawContext!!.matrices
        m.push()
        m.translate(position.x.toFloat(), position.y.toFloat(), 0F)
        context.root.render(ctx)
        m.pop()
        ctx.renderContext.doDrawTooltip()
    }

    override fun close() {
        dismiss()
    }
}
