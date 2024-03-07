package com.systemvi.engine.ui.utils.three

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.GestureDetector
import org.joml.Vector2f

import scala.collection.mutable
import scala.collection.mutable.Stack

class EventListenerFinder extends ThreeWalker{
  val stack=new mutable.Stack[GestureDetector]()
  var mouse: Vector2f = null
  private var mustContainMouse=true
  override def process(widget: Widget): Unit = widget match {
    case detector: GestureDetector=>stack.push(detector)
    case _ =>
  }
  override def shouldVisit(widget: Widget): Boolean = !mustContainMouse||widget.contains(mouse.x,mouse.y)
  def find(widget: Widget):mutable.Stack[GestureDetector]={
    mustContainMouse=false
    stack.clear()
    walk(widget)
    stack
  }
  def find(widget: Widget,mouse:Vector2f):mutable.Stack[GestureDetector]={
    mustContainMouse=true
    this.mouse=mouse
    stack.clear()
    walk(widget)
    stack
  }
}
