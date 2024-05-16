package com.systemvi.engine.model;

import com.systemvi.engine.buffer.ArrayBuffer;
import com.systemvi.engine.buffer.ElementsBuffer;
import com.systemvi.engine.buffer.VertexArray;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Model {
    public static class Vertex{
        public Vector3f position,normal,tangent,bitangent;
        public ArrayList<Vector3f> texCoords;
        public ArrayList<Vector4f> colors;
        public Vertex(Vector3f position, Vector3f normal, Vector3f tangent, Vector3f bitangent, ArrayList<Vector3f> texCoords, ArrayList<Vector4f> colors){
            this.position = position;
            this.normal = normal;
            this.tangent = tangent;
            this.bitangent = bitangent;
            this.texCoords = texCoords;
            this.colors = colors;
        }
    }
    public static class Face{
        public int[] indices;
        public Face(int[] indices){
            this.indices = indices;
        }
    }
    public static class Mesh{
        public ArrayList<Vertex> vertices;
        public ArrayList<Face> faces;
        public int materialIndex;
        public Material material;
        public String name;

        private final ArrayBuffer vertexBuffer;
        private final VertexArray vertexArray;
        private final ElementsBuffer elementsBuffer;

        public Mesh(String name,ArrayList<Vertex> vertices,Material material, int materialIndex,ArrayList<Face> faces) {
            this.vertices = vertices;
            this.material = material;
            this.materialIndex = materialIndex;
            this.name = name;
            this.faces=faces;
            vertexArray=new VertexArray();
            vertexBuffer=new ArrayBuffer();
            elementsBuffer=new ElementsBuffer();
            sendToGpu();
        }

        public void sendToGpu(){
            vertexArray.bind();
            vertexBuffer.bind();
            elementsBuffer.bind();

            int vertexSize=14;
            float[] vertexData=new float[vertices.size()*vertexSize];
            for(int i=0;i<vertices.size();i++){
                Vertex vertex=vertices.get(i);
                vertexData[i*vertexSize]=vertex.position.x;
                vertexData[i*vertexSize+1]=vertex.position.y;
                vertexData[i*vertexSize+2]=vertex.position.z;

                vertexData[i*vertexSize+3]=vertex.tangent.x;
                vertexData[i*vertexSize+4]=vertex.tangent.y;
                vertexData[i*vertexSize+5]=vertex.tangent.z;

                vertexData[i*vertexSize+6]=vertex.bitangent.x;
                vertexData[i*vertexSize+7]=vertex.bitangent.y;
                vertexData[i*vertexSize+8]=vertex.bitangent.z;

                vertexData[i*vertexSize+9]=vertex.normal.x;
                vertexData[i*vertexSize+10]=vertex.normal.y;
                vertexData[i*vertexSize+11]=vertex.normal.z;

                vertexData[i*vertexSize+12]=vertex.texCoords.get(0).x;
                vertexData[i*vertexSize+13]=vertex.texCoords.get(0).y;
            }
            vertexBuffer.setData(vertexData);
            vertexBuffer.setVertexAttributes(new VertexAttribute[]{
                new VertexAttribute("position",3),
                new VertexAttribute("tangent",3),
                new VertexAttribute("bitangent",3),
                new VertexAttribute("normal",3),
                new VertexAttribute("texCoords",2),
            });

            int elementsPerFace=3;
            int[] elementData=new int[faces.size()*elementsPerFace];
            for(int i=0;i<faces.size();i++){
                Face face=faces.get(i);
                elementData[i*elementsPerFace+0]=face.indices[0];
                elementData[i*elementsPerFace+1]=face.indices[1];
                elementData[i*elementsPerFace+2]=face.indices[2];
            }
            elementsBuffer.setData(elementData);

            vertexArray.unbind();
        }

        public void bind(){
            vertexArray.bind();
        }
        public void unbind(){
            vertexArray.unbind();
        }
        public void delete(){
            vertexBuffer.delete();
            vertexArray.delete();
        }
    }
    public static class Material{
        public final Vector4f ambient,diffuse,specular,emissive,reflective,transparent;
        public final String diffuseFile,specularFile,ambientOclusionFile,metalnessFile,displacementFile,roughnessFile,normalFile;
        public Material(
            Vector4f ambient,
            Vector4f diffuse,
            Vector4f specular,
            Vector4f emissive,
            Vector4f reflective,
            Vector4f transparent,
            String diffuseFile,
            String specularFile,
            String ambientOclusionFile,
            String metalnessFile,
            String displacementFile,
            String roughnessFile,
            String normalFile
            ){
            this.ambient = ambient;
            this.diffuse = diffuse;
            this.specular = specular;
            this.emissive=emissive;
            this.reflective=reflective;
            this.transparent=transparent;
            this.roughnessFile=roughnessFile;
            this.displacementFile=displacementFile;
            this.diffuseFile = diffuseFile;
            this.specularFile = specularFile;
            this.metalnessFile=metalnessFile;
            this.ambientOclusionFile = ambientOclusionFile;
            this.normalFile=normalFile;
        }
    }
    public static class Light{

    }
    public static class Node{
        public String name;
        public ArrayList<Node> children;
        public ArrayList<Integer> meshIndices;
        public ArrayList<Mesh> meshes;
        public Matrix4f transform;
        public Node(String name, ArrayList<Node> children, ArrayList<Integer> meshIndices, ArrayList<Mesh> meshes,Matrix4f transform){
            this.name = name;
            this.children = children;
            this.meshIndices = meshIndices;
            this.meshes = meshes;
            this.transform = transform;
        }
    }

    public ArrayList<Mesh> meshes;
    public ArrayList<Material> materials;
    public Node root;

    public Model(ArrayList<Mesh> meshes,ArrayList<Material> materials,Node root) {
        this.meshes = meshes;
        this.materials = materials;
        this.root=root;
    }
}
