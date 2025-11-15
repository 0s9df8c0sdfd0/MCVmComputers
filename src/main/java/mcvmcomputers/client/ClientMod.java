package mcvmcomputers.client;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.*;

import mcvmcomputers.MainMod;
import mcvmcomputers.client.entities.render.*;
import mcvmcomputers.client.gui.GuiCreateHarddrive;
import mcvmcomputers.client.gui.GuiFocus;
import mcvmcomputers.client.gui.GuiPCEditing;
import mcvmcomputers.client.tablet.TabletOS;
import mcvmcomputers.entities.EntityDeliveryChest;
import mcvmcomputers.entities.EntityItemPreview;
import mcvmcomputers.entities.EntityList;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.OrderableItem;
import mcvmcomputers.networking.NetworkHandler;
import mcvmcomputers.networking.PacketList;
import mcvmcomputers.utils.TabletOrder;
import mcvmcomputers.utils.TabletOrder.OrderStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.glfw.GLFW;
import org.virtualbox_6_1.ISession;
import org.virtualbox_6_1.IVirtualBox;
import org.virtualbox_6_1.VirtualBoxManager;

import com.mojang.blaze3d.platform.NativeImage;

@Mod.EventBusSubscriber(modid = "mcvmcomputers", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientMod {
    public static final OutputStream discardAllBytes = new OutputStream() { 
        @Override public void write(int b) throws IOException {} 
    };
    
    public static Map<UUID, ResourceLocation> vmScreenTextures;
    public static Map<UUID, NativeImage> vmScreenTextureNI;
    public static Map<UUID, DynamicTexture> vmScreenTextureDT;
    public static EntityItemPreview thePreviewEntity;
    public static boolean vmTurnedOn;
    public static boolean vmTurningOff;
    public static boolean vmTurningOn;
    public static ISession vmSession;
    
    public static int maxRam = 8192;
    public static int videoMem = 256;
    
    public static VirtualBoxManager vbManager;
    public static IVirtualBox vb;
    
    public static Process vboxWebSrv;
    public static Thread vmUpdateThread;
    public static byte[] vmTextureBytes;
    public static int vmTextureBytesSize;
    public static boolean failedSend;
    
    public static double mouseLastX = 0;
    public static double mouseLastY = 0;
    public static double mouseCurX = 0;
    public static double mouseCurY = 0;
    public static int mouseDeltaScroll;
    public static boolean leftMouseButton;
    public static boolean middleMouseButton;
    public static boolean rightMouseButton;
    public static List<Integer> vmKeyboardScancodes = new ArrayList<>();
    public static boolean releaseKeys = false;
    public static File vhdDirectory;
    public static File isoDirectory;
    public static int latestVHDNum = 0;
    public static TabletOS tabletOS;
    public static TabletOrder myOrder;
    public static int vmEntityID = -1;
    
    public static Thread tabletThread;
    
    public static float deltaTime;
    public static long lastDeltaTimeTime;
    
    public static int glfwUnfocusKey1;
    public static int glfwUnfocusKey2;
    public static int glfwUnfocusKey3;
    public static int glfwUnfocusKey4;
    
    static {
        if(SystemUtils.IS_OS_MAC) {
            glfwUnfocusKey1 = GLFW.GLFW_KEY_LEFT_ALT;
            glfwUnfocusKey2 = GLFW.GLFW_KEY_RIGHT_ALT;
            glfwUnfocusKey3 = GLFW.GLFW_KEY_BACKSPACE;
            glfwUnfocusKey4 = -1;
        } else {
            glfwUnfocusKey1 = GLFW.GLFW_KEY_LEFT_CONTROL;
            glfwUnfocusKey2 = GLFW.GLFW_KEY_RIGHT_CONTROL;
            glfwUnfocusKey3 = GLFW.GLFW_KEY_BACKSPACE;
            glfwUnfocusKey4 = -1;
        }
    }
    
    public static EntityDeliveryChest currentDeliveryChest;
    public static EntityPC currentPC;
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MainMod.pcOpenGui = new Runnable() {
                @Override
                public void run() {
                    Minecraft.getInstance().setScreen(new GuiPCEditing(currentPC));
                }
            };
            MainMod.hardDriveClick = new Runnable() {
                @Override
                public void run() {
                    Minecraft.getInstance().setScreen(new GuiCreateHarddrive());
                }
            };
            MainMod.focus = new Runnable() {
                @Override
                public void run() {
                    Minecraft.getInstance().setScreen(new GuiFocus());
                }
            };
            MainMod.deliveryChestSound = new Runnable() {
                @Override
                public void run() {
                    if(Minecraft.getInstance().getSoundManager().isActive(currentDeliveryChest.rocketSound)) {
                        Minecraft.getInstance().getSoundManager().stop(currentDeliveryChest.rocketSound);
                    }
                }
            };
            
            vmScreenTextures = new HashMap<>();
            vmScreenTextureNI = new HashMap<>();
            vmScreenTextureDT = new HashMap<>();
        });
    }
    
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityList.ITEM_PREVIEW.get(), ItemPreviewRender::new);
        event.registerEntityRenderer(EntityList.KEYBOARD.get(), KeyboardRender::new);
        event.registerEntityRenderer(EntityList.MOUSE.get(), MouseRender::new);
        event.registerEntityRenderer(EntityList.CRT_SCREEN.get(), CRTScreenRender::new);
        event.registerEntityRenderer(EntityList.FLATSCREEN.get(), FlatScreenRender::new);
        event.registerEntityRenderer(EntityList.WALLTV.get(), WallTVRender::new);
        event.registerEntityRenderer(EntityList.PC.get(), PCRender::new);
        event.registerEntityRenderer(EntityList.DELIVERY_CHEST.get(), DeliveryChestRender::new);
    }
    
    public static String getKeyName(int key) {
        if (key < 0) {
            return "None";
        } else {
            return glfwKey(key);
        }
    }
    
    private static String glfwKey(int key) {
        switch(key) {
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                return "L Control";
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                return "R Control";
            case GLFW.GLFW_KEY_RIGHT_ALT:
                return "R Alt";
            case GLFW.GLFW_KEY_LEFT_ALT:
                return "L Alt";
            case GLFW.GLFW_KEY_LEFT_SHIFT:
                return "L Shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
                return "R Shift";
            case GLFW.GLFW_KEY_ENTER:
                return "Enter";
            case GLFW.GLFW_KEY_BACKSPACE:
                return "Backspace";
            case GLFW.GLFW_KEY_CAPS_LOCK:
                return "Caps Lock";
            case GLFW.GLFW_KEY_TAB:
                return "Tab";
            default:
                return GLFW.glfwGetKeyName(key, 0);
        }
    }
    
    public static void getVHDNum() throws NumberFormatException, IOException {
        File f = new File(vhdDirectory.getParentFile(), "vhdnum");
        if(f.exists()) {
            latestVHDNum = Integer.parseInt(Files.readAllLines(f.toPath()).get(0));
        }
    }
    
    public static void increaseVHDNum() throws IOException {
        latestVHDNum++;
        File f = new File(vhdDirectory.getParentFile(), "vhdnum");
        if(f.exists()) {
            f.delete();
        }
        f.createNewFile();
        FileWriter fw = new FileWriter(f);
        fw.append(""+latestVHDNum);
        fw.flush();
        fw.close();
    }
    
    public static void generatePCScreen() {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) {
            return;
        }
        if(vmTextureBytes != null) {
            if(vmScreenTextures.containsKey(mc.player.getUUID())) {
                mc.getTextureManager().release(vmScreenTextures.get(mc.player.getUUID()));
                vmScreenTextures.remove(mc.player.getUUID());
            }
            
            Deflater def = new Deflater();
            def.setInput(vmTextureBytes);
            def.finish();
            byte[] deflated = new byte[vmTextureBytesSize];
            int sz = def.deflate(deflated);
            def.end();
            
            if(sz > 32766) {
                if(!failedSend){
                    // Send message - you'll need to use proper text components
                    failedSend = true;
                }
            } else {
                if(failedSend) {
                    // Send message
                    failedSend = false;
                }
                
                // Send packet to server
                NetworkHandler.sendToServer(new PacketList.C2SScreenPacket(
                    Arrays.copyOfRange(deflated, 0, sz), sz, vmTextureBytesSize
                ));
            }
            
            NativeImage ni = null;
            try {
                ni = NativeImage.read(new ByteArrayInputStream(vmTextureBytes));
            } catch (IOException e) {
            }
            if(ni != null) {
                if(vmScreenTextureNI.containsKey(mc.player.getUUID())) {
                    vmScreenTextureNI.get(mc.player.getUUID()).close();
                    vmScreenTextureNI.remove(mc.player.getUUID());
                }
                if(vmScreenTextureDT.containsKey(mc.player.getUUID())) {
                    vmScreenTextureDT.get(mc.player.getUUID()).close();
                    vmScreenTextureDT.remove(mc.player.getUUID());
                }
                vmScreenTextureNI.put(mc.player.getUUID(), ni);
                DynamicTexture dt = new DynamicTexture(ni);
                vmScreenTextureDT.put(mc.player.getUUID(), dt);
                vmScreenTextures.put(mc.player.getUUID(), 
                    mc.getTextureManager().register("vm_texture", dt));
            }
            vmTextureBytes = null;
        }
    }
}
