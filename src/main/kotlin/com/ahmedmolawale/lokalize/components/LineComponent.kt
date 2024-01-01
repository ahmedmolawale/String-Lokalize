package com.ahmedmolawale.lokalize.components

import java.awt.Component
import java.awt.Graphics

class LineComponent : Component() {
    override fun paint(g: Graphics) {
        super.paint(g)
        g.drawLine(0, 0, 1000, 0)
    }
}