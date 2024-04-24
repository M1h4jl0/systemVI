package com.systemvi.engine.ui

import com.systemvi.engine.model.{Mesh, VertexAttribute}
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import org.joml.{Matrix4f, Vector4f}

case class Rect(x:Float=0,y:Float=0,width:Float=0,height:Float=0,rotation:Float=0)
case class Border(radius:Float=0,width:Float=0,color:Vector4f=new Vector4f())
case class Drawable(rect:Rect=Rect(), color:Vector4f=new Vector4f(), border:Border=Border(), blur:Float=0, boundary:Rect=Rect(), glyph:Rect=Rect(), transform:Matrix4f=new Matrix4f()){
  def writeToArray(array:Array[Float],index:Int): Unit = {

  }
}
object Drawable{
  val size=40
}
class WidgetRenderer2(val view:Matrix4f,val projection:Matrix4f) {
  val mesh = new Mesh(
    new VertexAttribute("position",4)
  )
  val size=0.5f
  mesh.setVertexData(Array(
     size,  size ,0,1,
     size, -size ,0,1,
    -size, -size ,0,1,
    -size,  size ,0,1
  ))
  mesh.setIndices(Array(
    0,2,1,
    0,3,2
  ))
  mesh.enableInstancing(
    new VertexAttribute("rect",         4),
    new VertexAttribute("rectRotation", 1),
    new VertexAttribute("color",        4),
    new VertexAttribute("borderRadius", 1),
    new VertexAttribute("borderWidth",  1),
    new VertexAttribute("borderColor",  4),
    new VertexAttribute("blur",         1),
    new VertexAttribute("glyph",        4),
    new VertexAttribute("transformCol0",4),
    new VertexAttribute("transformCol1",4),
    new VertexAttribute("transformCol2",4),
    new VertexAttribute("transformCol3",4),
    new VertexAttribute("boundary",     4)
  )

  val shader=Shader.builder()
    .vertex("assets/renderer/widgetRenderer2/vertex.glsl")
    .fragment("assets/renderer/widgetRenderer2/fragment.glsl")
    .build()

  val maxInstances=1000
  var instancesToDraw=0
  val instanceData:Array[Float]=Array.ofDim(Drawable.size*maxInstances)
  def draw(drawable: Drawable): Unit = {
    if(instancesToDraw>=maxInstances)flush()
    drawable.writeToArray(instanceData,instancesToDraw)
    instancesToDraw+=1
  }
  def flush():Unit={
    shader.use()
    mesh.bind()
    mesh.setInstanceData(instanceData)
//    shader.drawElementsInstanced(Primitive.TRIANGLES,0,ElementsDataType.UNSIGNED_INT,6,1)
    mesh.drawInstancedElements(2,1)
  }
}
