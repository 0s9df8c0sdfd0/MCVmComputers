package mcvmcomputers;

import java.util.*;

import mcvmcomputers.entities.EntityList;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.networking.NetworkHandler;
import mcvmcomputers.sound.SoundList;
import mcvmcomputers.utils.TabletOrder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("mcvmcomputers")
public class MainMod {
    public static Map<UUID, TabletOrder> orders;
    public static Map<UUID, EntityPC> computers;
    
    public static Runnable hardDriveClick = new Runnable() { @Override public void run() {} };
    public static Runnable deliveryChestSound = new Runnable() { @Override public void run() {} };
    public static Runnable focus = new Runnable() { @Override public void run() {} };
    public static Runnable pcOpenGui = new Runnable() { @Override public void run() {} };
    
    public MainMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
        // Initialize registries
        ItemList.register(modEventBus);
        EntityList.register(modEventBus);
        SoundList.register(modEventBus);
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        // Initialize maps
        orders = new HashMap<>();
        computers = new HashMap<>();
        
        // Register network packets
        event.enqueueWork(() -> {
            NetworkHandler.register();
        });
    }
}
