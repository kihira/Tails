package uk.kihira.gltf;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import uk.kihira.gltf.spec.Accessor;
import uk.kihira.gltf.spec.BufferView;
import uk.kihira.gltf.spec.Gltf;
import uk.kihira.gltf.spec.Mesh.Primitive;
import uk.kihira.tails.common.IDisposable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

class Geometry implements IDisposable {
    private int drawMode = GL11.GL_TRIANGLES;
    private int vertexBuffer;
    private int indiciesBuffer;
    private int vertexCount;

    private Attribute positionAttribute;
    private Attribute normalAttribute;
    private Attribute texCoordAttribute;
    private Attribute indiciesAttribute;

    public void buildBuffers(Gltf gltf, ByteBuffer binData, Primitive primitive) throws IOException {
        Set<BufferView> bufferViews = new HashSet<>();
        int bufferSize = 0; // total size of the buffer to be created after combining bufferViews
        drawMode = primitive.mode.gl;

        // Load all attributes and their data
        for (Entry<Primitive.Attribute, Integer> attribute : primitive.attributes.entrySet()) {
            Accessor accessor = gltf.accessors.get(attribute.getValue());
            int itemBytes = accessor.type.size * accessor.componentType.size;

            BufferView bufferView = gltf.bufferViews.get(accessor.bufferView);

            // Store the buffer view if not loaded already and add it to total size
            if (!bufferViews.contains(bufferView)) {
                bufferViews.add(bufferView);
                bufferSize += bufferView.byteLength;
            }

            // If there is a stride and its not equal to its elements size, it's interleaved
            if (bufferView.byteStride != null && bufferView.byteStride != itemBytes) {
                // TODO something?
            }

            switch (attribute.getKey()) {
            case POSITION:
                positionAttribute = new Attribute(accessor.byteOffset, bufferView.byteStride, accessor.bufferView, accessor.componentType);
                vertexCount = accessor.count;
                break;
            case NORMAL:
                normalAttribute = new Attribute(accessor.byteOffset, bufferView.byteStride, accessor.bufferView, accessor.componentType);
                break;
            case TEXCOORD_0:
                texCoordAttribute = new Attribute(accessor.byteOffset, bufferView.byteStride, accessor.bufferView, accessor.componentType);
                break;
            default:
                throw new IOException(String.format("Attribute type %s not supported", attribute.getKey().name()));
            }
        }

        // Gen buffers and fill them with all that juicy data
        vertexBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferSize, GL15.GL_STATIC_DRAW);

        int offset = 0;
        for (BufferView bufferView : bufferViews) {
            ByteBuffer data = GltfLoader.LoadBufferView(gltf, binData, bufferView);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);

            offset += bufferView.byteLength;
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Load indicies if any exist
        if (primitive.indicies != null) {
            Accessor accessor = gltf.accessors.get(primitive.indicies);
            BufferView bufferView = gltf.bufferViews.get(accessor.bufferView);

            vertexCount = accessor.count;
            indiciesAttribute = new Attribute(accessor.byteOffset, 0, accessor.bufferView, accessor.componentType);
            indiciesBuffer = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesBuffer);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, GltfLoader.LoadBufferView(gltf, binData, bufferView), GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    public void render() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, positionAttribute.getStride(), positionAttribute.getOffset());

        if (normalAttribute != null) {
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glNormalPointer(GL11.GL_FLOAT, normalAttribute.getStride(), normalAttribute.getOffset());
        }
        if (positionAttribute != null) {
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(2, texCoordAttribute.getType().gl, texCoordAttribute.getStride(), texCoordAttribute.getOffset());
        }

        if (indiciesAttribute != null) {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesBuffer);
            GL11.glDrawElements(drawMode, vertexCount, indiciesAttribute.getType().gl, indiciesAttribute.getOffset());
        } else {
            GL11.glDrawArrays(drawMode, 0, vertexCount);
        }
    }

    @Override
    public void dispose() {
        GL15.glDeleteBuffers(vertexBuffer);
        if (indiciesBuffer > 0) GL15.glDeleteBuffers(indiciesBuffer);
    }
}