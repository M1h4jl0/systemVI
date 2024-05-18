package com.systemvi.examples.test3d.modelloading;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.camera.CameraController3;
import com.systemvi.engine.model.Model;
import com.systemvi.engine.model.ModelUtils;
import com.systemvi.engine.model.ModelLoaderParams;
import com.systemvi.engine.renderers.PhongRenderer;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class App extends Game {
    public App(){
        super(3,3,60,800,600,"Model Loading");
    }

    public Model[] model;
    public CameraController3 controller;
    public PhongRenderer[] renderer;
    public String[] modelFiles=new String[]{
        "assets/examples/models/cars/sedan-sports.glb",
        "assets/examples/models/castle/tower.glb",
        "assets/examples/models/castle/bridge-draw.glb",
        "assets/examples/models/castle/bridge-straight-pillar.obj",
        "assets/examples/models/castle/bridge-straight.obj",
        "assets/examples/models/castle/siege-ballista.fbx"
    };

    @Override
    public void setup(Window window) {
        controller=CameraController3.builder()
            .camera(Camera3.builder3d()
                .build())
            .speed(3)
            .aspect((float) window.getWidth() /window.getHeight())
            .window(window)
            .build();
        setInputProcessor(controller);
        model=new Model[modelFiles.length];
        renderer=new PhongRenderer[modelFiles.length];
        for(int i=0;i<model.length;i++){
            model[i]=ModelUtils.load(
                ModelLoaderParams.builder()
                    .fileName(modelFiles[i])
                    .triangulate()
                    .calcTangentSpace()
                    .genSmoothNormals()
                    .fixInfacingNormals()
                    .genSmoothNormals()
                    .flipUVs()
                    .joinIdenticalVertices()
                    .build()
            );
            renderer[i]= PhongRenderer.builder()
                .camera(controller.camera())
                .model(model[i])
                .build();
        }
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0,0,0,1, Utils.Buffer.COLOR_BUFFER, Utils.Buffer.DEPTH_BUFFER);
        controller.update(delta);
        for(int i=0;i<renderer.length;i++){
            float scale=1;
            if(i==renderer.length-1)scale=0.01f;
            renderer[i].render(new Matrix4f().translate(i*2,0,0).scale(scale));
        }
    }
}
