package com.systemvi.examples.sdf

import com.systemvi.engine.math.sdf.{Plane, Sphere, Union}
import com.systemvi.engine.ui.utils.data.Colors
import org.joml.Vector3f

import scala.util.Random

object Map {
  val Epsilon: Float = 0.001f
  val random = new Random()

  val materials: Array[Material] = Array[Material](
    Material(0.3f, 1f, Colors.red500), //sphere red
    Material(0.3f, 1f, Colors.green500), //sphere green
    Material(0.3f, 1f, Colors.orange500), //floor orange
    Material(0.3f, 1, Colors.blue100)
  ) //sky

  val spawnRadius=100
  val sphereCount=5
  case class Ball(center:Vector3f,radius:Float)
  var spheres: Array[Ball] = (0 until sphereCount).map{_=>
    var insideCircle=false
    var x=0f
    var y=0f
    var z=0f
    var r=0f
    while(!insideCircle) {
      x = random.nextInt(spawnRadius*2)-spawnRadius
      z = random.nextInt(spawnRadius*2)-spawnRadius
      val d=Math.sqrt(x*x+y*y).toFloat
      r = (spawnRadius-d)/spawnRadius*10f+3f
      y = r
      if(d<spawnRadius)insideCircle=true
    }
    Ball(new Vector3f(x,y,z),r)
  }.toArray

  def getMaterial(p: Vector3f): Material = {
    for (i <- 0 until spheres.length) {
      if (Sphere(p, spheres(i).center, spheres(i).radius) < Epsilon) return materials(i % (materials.length - 1))
    }
    if (Plane(p, new Vector3f(0, 0, 0)) < Epsilon) return Material(0.3f, 1f, Colors.yellow400)
    materials(3)
  }

  def getDistance(p: Vector3f): Float = {
    Union(
      spheres.map { s => Sphere(p, s.center, s.radius)} :+ Plane(p, new Vector3f(0, 0, 0)):_*
    )
  }
}
