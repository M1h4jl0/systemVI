package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.Scene
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.widgets.{Center, Container, EdgeInsets, Padding, Text}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

class TextWidgetTest extends Game(3,3,60,800,600,"Text Widget"){
  var scene:Scene=null

  override def setup(window: Window): Unit = {
    val font=Font.load(
      "assets/examples/widgetRenderer2Test/font.PNG",
      "assets/examples/widgetRenderer2Test/font.json"
    )
    scene=Scene(
      initialWidth = window.getWidth,
      initialHeight = window.getHeight,
      font=font,
      root=Center(
        child=Container(
          decoration=BoxDecoration(
            color = Colors.green500, borderRadius = 20
          ),
          child = Padding(
            padding=EdgeInsets.all(10),
            child=Text("Ovo je neki dugacak text\nOvo je neki dugacak text\nOvo je neki dugacak text\nOvo je neki dugacak text\n",font=font)
          )
        )
      )
    )
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,1,Buffer.COLOR_BUFFER)
    scene.animate(delta)
    scene.draw()
  }
}
