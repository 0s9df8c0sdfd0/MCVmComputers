package mcvmcomputers.networking;

import mcvmcomputers.networking.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    
    private static int id() {
        return packetId++;
    }
    
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation("mcvmcomputers", "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        
        INSTANCE = net;
        
        // Client to Server packets
        net.messageBuilder(C2SOrderPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SOrderPacket::new)
                .encoder(C2SOrderPacket::toBytes)
                .consumerMainThread(C2SOrderPacket::handle)
                .add();
        
        net.messageBuilder(C2SScreenPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SScreenPacket::new)
                .encoder(C2SScreenPacket::toBytes)
                .consumerMainThread(C2SScreenPacket::handle)
                .add();
        
        net.messageBuilder(C2STurnOnPCPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2STurnOnPCPacket::new)
                .encoder(C2STurnOnPCPacket::toBytes)
                .consumerMainThread(C2STurnOnPCPacket::handle)
                .add();
        
        net.messageBuilder(C2STurnOffPCPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2STurnOffPCPacket::new)
                .encoder(C2STurnOffPCPacket::toBytes)
                .consumerMainThread(C2STurnOffPCPacket::handle)
                .add();
        
        net.messageBuilder(C2SChangeHDDPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SChangeHDDPacket::new)
                .encoder(C2SChangeHDDPacket::toBytes)
                .consumerMainThread(C2SChangeHDDPacket::handle)
                .add();
        
        net.messageBuilder(C2SAddMoboPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SAddMoboPacket::new)
                .encoder(C2SAddMoboPacket::toBytes)
                .consumerMainThread(C2SAddMoboPacket::handle)
                .add();
        
        net.messageBuilder(C2SAddGPUPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SAddGPUPacket::new)
                .encoder(C2SAddGPUPacket::toBytes)
                .consumerMainThread(C2SAddGPUPacket::handle)
                .add();
        
        net.messageBuilder(C2SAddCPUPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SAddCPUPacket::new)
                .encoder(C2SAddCPUPacket::toBytes)
                .consumerMainThread(C2SAddCPUPacket::handle)
                .add();
        
        net.messageBuilder(C2SAddRAMPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SAddRAMPacket::new)
                .encoder(C2SAddRAMPacket::toBytes)
                .consumerMainThread(C2SAddRAMPacket::handle)
                .add();
        
        net.messageBuilder(C2SAddHardDrivePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SAddHardDrivePacket::new)
                .encoder(C2SAddHardDrivePacket::toBytes)
                .consumerMainThread(C2SAddHardDrivePacket::handle)
                .add();
        
        net.messageBuilder(C2SRemoveMoboPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRemoveMoboPacket::new)
                .encoder(C2SRemoveMoboPacket::toBytes)
                .consumerMainThread(C2SRemoveMoboPacket::handle)
                .add();
        
        net.messageBuilder(C2SRemoveGPUPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRemoveGPUPacket::new)
                .encoder(C2SRemoveGPUPacket::toBytes)
                .consumerMainThread(C2SRemoveGPUPacket::handle)
                .add();
        
        net.messageBuilder(C2SRemoveHardDrivePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRemoveHardDrivePacket::new)
                .encoder(C2SRemoveHardDrivePacket::toBytes)
                .consumerMainThread(C2SRemoveHardDrivePacket::handle)
                .add();
        
        net.messageBuilder(C2SRemoveCPUPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRemoveCPUPacket::new)
                .encoder(C2SRemoveCPUPacket::toBytes)
                .consumerMainThread(C2SRemoveCPUPacket::handle)
                .add();
        
        net.messageBuilder(C2SRemoveRAMPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRemoveRAMPacket::new)
                .encoder(C2SRemoveRAMPacket::toBytes)
                .consumerMainThread(C2SRemoveRAMPacket::handle)
                .add();
        
        net.messageBuilder(C2SAddISOPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SAddISOPacket::new)
                .encoder(C2SAddISOPacket::toBytes)
                .consumerMainThread(C2SAddISOPacket::handle)
                .add();
        
        net.messageBuilder(C2SRemoveISOPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRemoveISOPacket::new)
                .encoder(C2SRemoveISOPacket::toBytes)
                .consumerMainThread(C2SRemoveISOPacket::handle)
                .add();
        
        // Server to Client packets
        net.messageBuilder(S2CScreenPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CScreenPacket::new)
                .encoder(S2CScreenPacket::toBytes)
                .consumerMainThread(S2CScreenPacket::handle)
                .add();
        
        net.messageBuilder(S2CStopScreenPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CStopScreenPacket::new)
                .encoder(S2CStopScreenPacket::toBytes)
                .consumerMainThread(S2CStopScreenPacket::handle)
                .add();
        
        net.messageBuilder(S2CSyncOrderPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CSyncOrderPacket::new)
                .encoder(S2CSyncOrderPacket::toBytes)
                .consumerMainThread(S2CSyncOrderPacket::handle)
                .add();
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
