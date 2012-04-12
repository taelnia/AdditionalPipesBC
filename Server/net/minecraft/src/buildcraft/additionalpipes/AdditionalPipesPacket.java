package net.minecraft.src.buildcraft.additionalpipes;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.buildcraft.core.network.PacketPayload;
import net.minecraft.src.forge.packets.ForgePacket;

public class AdditionalPipesPacket extends ForgePacket {
    
    private int packetId;
    private boolean isChunkPacket = true;
    public int posX, posY, posZ;
    
    public PacketPayload payload;

    public AdditionalPipesPacket(int packetId) {
        this.packetId = packetId;
    }
    
    public AdditionalPipesPacket(int packetId, PacketPayload payload) {
        
        this.packetId  = packetId;
        this.payload = payload;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        
        data.writeInt(posX);
        data.writeInt(posY);
        data.writeInt(posZ);

        // No payload means no data
        if (payload == null) {
            data.writeInt(0);
            data.writeInt(0);
            data.writeInt(0);
            return;
        }

        data.writeInt(payload.intPayload.length);
        data.writeInt(payload.floatPayload.length);
        data.writeInt(payload.stringPayload.length);

        for (int intData : payload.intPayload) {
            data.writeInt(intData);
        }
        for (float floatData : payload.floatPayload) {
            data.writeFloat(floatData);
        }
        for (String stringData : payload.stringPayload) {
            data.writeUTF(stringData);
        }
        
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        
        posX = data.readInt();
        posY = data.readInt();
        posZ = data.readInt();

        payload = new PacketPayload();

        payload.intPayload = new int[data.readInt()];
        payload.floatPayload = new float[data.readInt()];
        payload.stringPayload = new String[data.readInt()];

        for (int i = 0; i < payload.intPayload.length; i++) {
            payload.intPayload[i] = data.readInt();
        }
        for (int i = 0; i < payload.floatPayload.length; i++) {
            payload.floatPayload[i] = data.readFloat();
        }
        for (int i = 0; i < payload.stringPayload.length; i++) {
            payload.stringPayload[i] = data.readUTF();
        }
    }

    @Override
    public Packet getPacket() {
        
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        
        try {
            data.writeByte(getID());
            writeData(data);
        } catch (IOException e) {
            
        }
        
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = NetworkHandler.CHANNEL;
        packet.data = bytes.toByteArray();
        packet.length = packet.data.length;
        packet.isChunkDataPacket = isChunkPacket;
        return packet;
    }

    @Override
    public int getID() {
        return packetId;
    }

}