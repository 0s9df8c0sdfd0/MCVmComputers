package mcvmcomputers.networking;

import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemHarddrive;
import mcvmcomputers.item.ItemList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PCComponentUtils {
    
    public static void removeGpu(EntityPC pc) {
        if(pc.getGpuInstalled()) {
            pc.setGpuInstalled(false);
            pc.level().addFreshEntity(new ItemEntity(pc.level(), pc.getX(), pc.getY(), pc.getZ(), 
                new ItemStack(ItemList.ITEM_GPU.get())));
        }
    }
    
    public static void removeHdd(EntityPC pc, String uuid) {
        if(!pc.getHardDriveFileName().isEmpty()) {
            pc.level().addFreshEntity(new ItemEntity(pc.level(), pc.getX(), pc.getY(), pc.getZ(), 
                ItemHarddrive.createHardDrive(pc.getHardDriveFileName())));
            pc.setHardDriveFileName("");
        }
    }
    
    public static void removeCpu(EntityPC pc) {
        if(pc.getCpuDividedBy() > 0) {
            Item cpuItem = null;
            switch(pc.getCpuDividedBy()) {
                case 2:
                    cpuItem = ItemList.ITEM_CPU2.get();
                    break;
                case 4:
                    cpuItem = ItemList.ITEM_CPU4.get();
                    break;
                case 6:
                    cpuItem = ItemList.ITEM_CPU6.get();
                    break;
                default:
                    return;
            }
            pc.setCpuDividedBy(0);
            pc.level().addFreshEntity(new ItemEntity(pc.level(), pc.getX(), pc.getY(), pc.getZ(), 
                new ItemStack(cpuItem)));
        }
    }
    
    public static void removeRam(EntityPC pc, int slot) {
        Item ramStickItem = null;
        if(slot == 0) {
            if(pc.getGigsOfRamInSlot0() == 1024) {
                ramStickItem = ItemList.ITEM_RAM1G.get();
            } else if(pc.getGigsOfRamInSlot0() == 2048) {
                ramStickItem = ItemList.ITEM_RAM2G.get();
            } else if(pc.getGigsOfRamInSlot0() == 4096) {
                ramStickItem = ItemList.ITEM_RAM4G.get();
            } else if(pc.getGigsOfRamInSlot0() == 256) {
                ramStickItem = ItemList.ITEM_RAM256M.get();
            } else if(pc.getGigsOfRamInSlot0() == 512) {
                ramStickItem = ItemList.ITEM_RAM512M.get();
            } else if(pc.getGigsOfRamInSlot0() == 128) {
                ramStickItem = ItemList.ITEM_RAM128M.get();
            } else if(pc.getGigsOfRamInSlot0() == 64) {
                ramStickItem = ItemList.ITEM_RAM64M.get();
            } else {
                return;
            }
            pc.setGigsOfRamInSlot0(0);
            pc.level().addFreshEntity(new ItemEntity(pc.level(), pc.getX(), pc.getY(), pc.getZ(), 
                new ItemStack(ramStickItem)));
        } else {
            if(pc.getGigsOfRamInSlot1() == 1024) {
                ramStickItem = ItemList.ITEM_RAM1G.get();
            } else if(pc.getGigsOfRamInSlot1() == 2048) {
                ramStickItem = ItemList.ITEM_RAM2G.get();
            } else if(pc.getGigsOfRamInSlot1() == 4096) {
                ramStickItem = ItemList.ITEM_RAM4G.get();
            } else if(pc.getGigsOfRamInSlot1() == 256) {
                ramStickItem = ItemList.ITEM_RAM256M.get();
            } else if(pc.getGigsOfRamInSlot1() == 512) {
                ramStickItem = ItemList.ITEM_RAM512M.get();
            } else if(pc.getGigsOfRamInSlot1() == 128) {
                ramStickItem = ItemList.ITEM_RAM128M.get();
            } else if(pc.getGigsOfRamInSlot1() == 64) {
                ramStickItem = ItemList.ITEM_RAM64M.get();
            } else {
                return;
            }
            pc.setGigsOfRamInSlot1(0);
            pc.level().addFreshEntity(new ItemEntity(pc.level(), pc.getX(), pc.getY(), pc.getZ(), 
                new ItemStack(ramStickItem)));
        }
    }
}
