package com.systemvi.engine.camera;

import com.systemvi.engine.window.InputProcessor;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class CameraController2 implements InputProcessor {
    public Camera camera;
    public float x,y,z,roll,pitch,yaw;
    public float sensitivity,speed;
    public boolean forward,backwards,left,right,up,down,mouseDown;
    public float mx,my;
    private boolean captured,flipX,flipY;

    public CameraController2(Camera camera,float x,float y,float z,float roll,float pitch,float yaw){
        this.camera=camera;
        this.x=x;
        this.y=y;
        this.z=z;
        this.roll=roll;
        this.pitch=pitch;
        this.yaw=yaw;
        forward=false;
        backwards=false;
        left=false;
        right=false;
        down=false;
        up=false;
        mx=0;
        my=0;
        mouseDown=false;
        sensitivity=0.01f;
        speed=1;
        captured=true;
        flipX=false;
        flipY=false;
    }



    public CameraController2(Camera camera){
        this(camera,0,0,0,0,0,0);
    }
    public static class Builder{
        private float yaw,pitch,roll;
        private float x,y,z;
        private boolean flipX,flipY;
        private Camera camera;
        private float sensitivity,speed;
        public Builder(){
            yaw=0;pitch=0;roll=0;
            x=0;y=0;z=0;
            flipX=false;flipY=false;
            camera=null;
            sensitivity=0.01f;speed=10;
        }
        public Builder position(float x,float y,float z){
            this.x=x;this.y=y;this.z=z;
            return this;
        }
        public Builder direction(float yaw,float pitch,float roll){
            this.yaw=yaw;this.pitch=pitch;this.roll=roll;
            return this;
        }
        public Builder flip(boolean flipX,boolean flipY){
            this.flipX=flipX;
            this.flipY=flipY;
            return this;
        }
        public Builder camera(Camera camera){
            this.camera=camera;
            return this;
        }
        public Builder sensitivity(float sensitivity){
            this.sensitivity=sensitivity;
            return this;
        }
        public Builder speed(float speed){
            this.speed=speed;
            return this;
        }
        public CameraController2 build(){
            CameraController2 controller=new CameraController2(camera,x,y,z,roll,pitch,yaw);
            controller.sensitivity=sensitivity;
            controller.speed=speed;
            controller.flipX=flipX;
            controller.flipY=flipY;
            return controller;
        }
    }
    public static Builder builder(){
        return new Builder();
    }
    public void setCaptured(boolean captured) {
        this.captured = captured;
        if(captured){

        }else{

        }
    }

    @Override
    public boolean keyDown(int key, int scancode, int mods) {
//        if(captured){
            if(key==GLFW_KEY_W)forward=true;
            if(key==GLFW_KEY_S)backwards=true;
            if(key==GLFW_KEY_A)left=true;
            if(key==GLFW_KEY_D)right=true;
            if(key==GLFW_KEY_SPACE)up=true;
            if(key==GLFW_KEY_LEFT_SHIFT)down=true;
            return true;
//        }
//        return false;
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
//        if(captured){
            if(key==GLFW_KEY_W)forward=false;
            if(key==GLFW_KEY_S)backwards=false;
            if(key==GLFW_KEY_A)left=false;
            if(key==GLFW_KEY_D)right=false;
            if(key==GLFW_KEY_SPACE)up=false;
            if(key==GLFW_KEY_LEFT_SHIFT)down=false;
            return true;
//        }
//        return false;
    }

    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        mx=(float) x;
        my=(float) y;
        mouseDown=true;
        return true;
    }

    @Override
    public boolean mouseUp(int button, int mods, double x, double y) {
        mx=(float) x;
        my=(float) y;
        mouseDown=false;
        return true;
    }

    @Override
    public boolean mouseMove(double x, double y) {
        if(mouseDown){
            yaw+= (float) ((x-mx)*sensitivity)*(flipX?-1:1);
            pitch+= (float) ((y-my)*sensitivity)*(flipY?-1:1);
            if(pitch>Math.PI/2-0.1f)pitch=(float) Math.PI/2-0.1f;
            if(pitch<-Math.PI/2+0.1f)pitch=(float) -Math.PI/2+0.1f;
        }
        mx= (float) x;
        my= (float) y;
        return true;
    }

    public void update(float delta){
        Vector2f forwardDir=new Vector2f(
            (float) (Math.cos(yaw)*speed*delta),
            (float) (Math.sin(yaw)*speed*delta)
        );
        Vector2f rightDir=new Vector2f(
            (float) (Math.cos(yaw+Math.toRadians(90))*speed*delta),
            (float) (Math.sin(yaw+Math.toRadians(90))*speed*delta)
        );

        if(forward){x+=forwardDir.x;z+=forwardDir.y;}
        if(backwards){x-=forwardDir.x;z-=forwardDir.y;}
        if(left){x-=rightDir.x;z-=rightDir.y;}
        if(right){x+=rightDir.x;z+=rightDir.y;}
        if(up){y+=speed*delta;}
        if(down){y-=speed*delta;}

        Vector3f dir=new Vector3f();
        dir.x= (float) (Math.cos(yaw)*Math.cos(pitch));
        dir.y= (float) Math.sin(pitch);
        dir.z= (float) (Math.sin(yaw)*Math.cos(pitch));

        camera.lookAt(dir);

        camera.setPosition(x,y,z);
        camera.update();
    }

    @Override
    public boolean scroll(double offsetX, double offsetY) {
        return true;
    }

    @Override
    public boolean resize(int width, int height) {
        return true;
    }
}
