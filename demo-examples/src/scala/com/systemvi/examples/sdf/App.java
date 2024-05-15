package com.systemvi.examples.sdf;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.camera.CameraController3;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureData;
import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class App extends Game {
    public App(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    public App(){
        this(3, 3, 60, 800, 600, "SDFs");
    }

    Texture texture;
    Camera3 camera, worldCamera;
    TextureRenderer renderer;
    final float Epsilon = 0.001f;
    public Random r=new Random();
    public TextureData data;
    public CameraController3 controller;
    public Material[] materials=new Material[]{
        new Material(0,1f, Colors.red500()),   //sphere red
        new Material(0,1f, Colors.green500()),   //sphere green
        new Material(0,1f, Colors.orange500()),   //floor orange
        new Material(0,1, Colors.blue100())    //sky
    };
    ExecutorService service;// = Executors.newFixedThreadPool(threads);
    Future[] futures;//=new Future[tasks];

    public float Map(Vector3f p){
        return SDF.Union(SDF.Sphere(
                SDF.Translate(p, new Vector3f(0, 0, -200)),
                100
            ), SDF.Sphere(
                SDF.Translate(p, new Vector3f(-100, 200, -300)),
                100),
            SDF.Plain(SDF.Translate(p, new Vector3f(0, -100, 0))));
    }

    public Vector3f RayMarch(Vector3f ro, Vector3f rd,int iterations){
        float d = 0;
        for (int k = 0; k < iterations; k++) {
            Vector3f p = new Vector3f(
                ro.x + rd.x * d,
                ro.y + rd.y * d,
                ro.z + rd.z * d
            );
            d += Map(p);
            if (d > 1000) break;
            if (Map(p) < Epsilon) break;
        }
        return new Vector3f(
            ro.x + rd.x * d,
            ro.y + rd.y * d,
            ro.z + rd.z * d
        );
    }

    public Vector3f getNormal(Vector3f p){
        return new Vector3f(
            Map(new Vector3f(p.x + 0.01f, p.y, p.z)) - Map(new Vector3f(p.x - 0.01f, p.y, p.z)),
            Map(new Vector3f(p.x, p.y + 0.01f, p.z)) - Map(new Vector3f(p.x, p.y - 0.01f, p.z)),
            Map(new Vector3f(p.x, p.y, p.z + 0.01f)) - Map(new Vector3f(p.x, p.y, p.z - 0.01f))
        ).normalize();
    }

    public Material getMaterial(Vector3f p){
        if (SDF.Sphere(
            SDF.Translate(p, new Vector3f(0, 0, -200)),
            100
        ) < Epsilon)
            return materials[0];

        if (SDF.Sphere(
            SDF.Translate(p, new Vector3f(-100, 200, -300)),
            100
        ) < Epsilon)
            return materials[1];

        if (SDF.Plain(SDF.Translate(p, new Vector3f(0, -100, 0))) < Epsilon)
            return materials[2];

        return materials[3];
    }

    public Vector4f SimulatePhoton(float x, float y, int bounces, Random r,int iterations){
        Vector4f color = new Vector4f(1);
        Vector3f[] ro = new Vector3f[bounces + 1];
        Vector3f[] rd = new Vector3f[bounces + 1];

        Matrix4f inverted=new Matrix4f(worldCamera.view()).invert();
        Vector4f focus=new Vector4f(0,0,2.2f,1).mul(inverted);
        Vector4f point=new Vector4f(x,y,0,1).mul(inverted);

        ro[0] = new Vector3f(focus.x,focus.y,focus.z);
        rd[0] = new Vector3f(point.x,point.y,point.z).sub(ro[0]).normalize();

        for (int k = 0; k < bounces; k++) {
            Vector3f p = RayMarch(ro[k], rd[k],iterations);
            Vector3f normal = getNormal(p);
            Material m = getMaterial(p);
            Vector4f c = m.color;

            if (p.distance(ro[k]) > 1000)
                break;

            ro[k + 1] = new Vector3f(p).add(normal.x * 2 * Epsilon, normal.y * 2 * Epsilon, normal.z * 2 * Epsilon);
            rd[k + 1] = new Vector3f(rd[k]).reflect(normal).add(new Vector3f(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1).mul(m.roughness)).normalize();
//            rd[k + 1] = new Vector3f(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1).normalize();
//            if (rd[k + 1].dot(normal) < 0)
//                rd[k + 1].mul(-1);

//            if (r.nextFloat() < m.metallic || k == 0)
            if (r.nextFloat() < m.metallic)
                color.mul(c);
        }
        return color;
    }
    public Vector4f calculatePixel(int i,int j,int bounces,int samples,int iterations){
        float x, y;
        x = i;
        x /= 800;
        x = x*2-1;
        x *= 800f / 600f;
        y = j;
        y /= 600;
        y = 2*y-1;
        y *= -1;

        Vector4f color = new Vector4f(0);

        for (int k = 0; k < samples; k++) {
            color.add(SimulatePhoton(x, y, bounces, r,iterations));
        }
        color.div(samples);
        return color;
    }

    @Override
    public void setup(Window window) {
        camera = Camera3.builder2d().size(800, 600).position(400, 300).scale(1, -1).build();

        worldCamera = Camera3.builder3d()
            .build();
        controller = CameraController3.builder()
            .window(window)
            .camera(worldCamera)
            .speed(50)
            .build();
        setInputProcessor(controller);

        texture = new Texture(800, 600, Format.RGBA);
        data = new TextureData(800, 600, Format.RGBA);

//        for(int i=6;i<32;i++){
//            int attempts=10;
//            float average=0;
//            for(int j=0;j<attempts;j++){
//                long startTime=System.nanoTime();
//                renderMultiThread(i);
//                long endTime=System.nanoTime();
//                average+=(endTime-startTime)/1000000f;
//            }
//            System.out.println(i+" Threads: "+(average/attempts));
//        }

//        renderMultiThread(16,32,3,10,1000);

        renderer = new TextureRenderer();
        renderer.view(camera.view());
        renderer.projection(camera.projection());
        int threads=32,tasks=32;
        service = Executors.newFixedThreadPool(threads);
        futures=new Future[tasks];
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0.4f, 0, 0, 0, Utils.Buffer.COLOR_BUFFER);
        System.out.printf("%4d \r",getFPS());
        controller.update(delta);

//        for(int i=0;i<1000;i++){
//            int x=r.nextInt(800),y=r.nextInt(600);
//            try{
//                data.setPixel4f(x,y,calculatePixel(x,y));
//            }catch (Exception e){
//                System.out.println(x+" "+y);
//                close();
//                break;
//            }
//        }
//        renderMultiThread(16,32,2,1,100);
        renderMultiThread(4,1,100);
        texture.setData(data);
        renderer.draw(texture, 0, 0, 800, 600);
        renderer.flush();
    }

    @Override
    public void dispose() {
        try{
            service.shutdown();
            service.awaitTermination(0, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void renderSingleThread(int bounces, int samples, int iterations){
        long startTime=System.nanoTime();
        for(int i=0;i<800;i++){
                    for(int j=0;j<600;j++){
                        int x=i;
                        int y=j;
                        data.setPixel4f(x,y,calculatePixel(x,y,bounces,samples,iterations));
                    }
                }
        long endTime=System.nanoTime();
        System.out.println((endTime-startTime)/1000_000f);
    }
    public void renderMultiThread(int threads,int tasks,int bounces,int samples,int iterations){
        long startTime=System.nanoTime();
        ExecutorService service = Executors.newFixedThreadPool(threads);
        Future[] futures=new Future[tasks];
        for(int k=0;k<tasks;k++){
            final int index=k;
            futures[k]=service.submit(()->{
                for(int i=0;i<800/tasks;i++){
                    for(int j=0;j<600;j++){
                        int x=index*800/tasks+i;
                        int y=j;
                        data.setPixel4f(x,y,calculatePixel(x,y,bounces,samples,iterations));
                    }
                }
            });
        }
        try{
            for(int k=0;k<tasks;k++){futures[k].get();}
            service.shutdown();
            service.awaitTermination(0, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }
        long endTime=System.nanoTime();
//        System.out.println((endTime-startTime)/1000_000f);
    }

    public void renderMultiThread(int bounces,int samples,int iterations){
        int tasks=futures.length;
        for(int k=0;k<tasks;k++){
            final int index=k;
            futures[k]=service.submit(()->{
                for(int i=0;i<800/tasks;i++){
                    for(int j=0;j<600;j++){
                        int x=index*800/tasks+i;
                        int y=j;
                        data.setPixel4f(x,y,calculatePixel(x,y,bounces,samples,iterations));
                    }
                }
            });
        }
        try{
            for(int k=0;k<tasks;k++){futures[k].get();}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
