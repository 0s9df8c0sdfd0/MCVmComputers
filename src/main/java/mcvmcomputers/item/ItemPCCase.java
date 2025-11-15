package mcvmcomputers.item;

import java.util.List;

import mcvmcomputers.MainMod;
import mcvmcomputers.client.ClientMod;
import mcvmcomputers.entities.EntityPC;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ItemPCCase extends OrderableItem {
    public ItemPCCase(Properties properties) {
        super(properties, 2);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            player.getItemInHand(hand).shrink(1);
            HitResult hr = player.pick(10, 0f, false);
            EntityPC ek = new EntityPC(level, 
                                    hr.getLocation().x,
                                    hr.getLocation().y,
                                    hr.getLocation().z,
                                    new Vec3(player.getX(),
                                            hr.getLocation().y,
                                            player.getZ()), 
                                    player.getUUID(), 
                                    player.getItemInHand(hand).getTag());
            level.addFreshEntity(ek);
            MainMod.computers.put(player.getUUID(), ek);
        }
        
        if(level.isClientSide) {
            level.playSound(null,
                            ClientMod.thePreviewEntity.getX(),
                            ClientMod.thePreviewEntity.getY(),
                            ClientMod.thePreviewEntity.getZ(),
                            SoundEvents.METAL_PLACE,
                            SoundSource.BLOCKS, 1, 1);
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if(stack.getTag() != null) {
            if (stack.getTag().contains("MoboInstalled")) {
                if(stack.getTag().getBoolean("MoboInstalled")) {
                    tooltip.add(Component.translatable(stack.getTag().getBoolean("x64") ? 
                        "item.mcvmcomputers.motherboard64" : "item.mcvmcomputers.motherboard")
                        .withStyle(ChatFormatting.GRAY));
                    
                    if(stack.getTag().getBoolean("GPUInstalled"))
                        tooltip.add(Component.translatable("mcvmcomputers.pc_item_gpu")
                            .withStyle(ChatFormatting.GRAY));
                    
                    if(stack.getTag().getInt("CPUDividedBy") > 0)
                        tooltip.add(Component.translatable("mcvmcomputers.pc_item_cpu", 
                            stack.getTag().getInt("CPUDividedBy"))
                            .withStyle(ChatFormatting.GRAY));
                    
                    if(stack.getTag().getInt("RAMSlot0") > 0) {
                        if((stack.getTag().getInt("RAMSlot0") / 1024) < 1) {
                            tooltip.add(Component.translatable("mcvmcomputers.pc_item_ramSlot0Mb", 
                                stack.getTag().getInt("RAMSlot0"))
                                .withStyle(ChatFormatting.GRAY));
                        } else if((stack.getTag().getInt("RAMSlot0") / 1024) >= 1) {
                            tooltip.add(Component.translatable("mcvmcomputers.pc_item_ramSlot0", 
                                (stack.getTag().getInt("RAMSlot0") / 1024))
                                .withStyle(ChatFormatting.GRAY));
                        }
                    }
                    
                    if(stack.getTag().getInt("RAMSlot1") > 0) {
                        if((stack.getTag().getInt("RAMSlot1") / 1024) < 1) {
                            tooltip.add(Component.translatable("mcvmcomputers.pc_item_ramSlot1Mb", 
                                stack.getTag().getInt("RAMSlot1"))
                                .withStyle(ChatFormatting.GRAY));
                        } else if((stack.getTag().getInt("RAMSlot1") / 1024) >= 1) {
                            tooltip.add(Component.translatable("mcvmcomputers.pc_item_ramSlot1", 
                                (stack.getTag().getInt("RAMSlot1") / 1024))
                                .withStyle(ChatFormatting.GRAY));
                        }
                    }
                    
                    if(!stack.getTag().getString("VHDName").isEmpty())
                        tooltip.add(Component.translatable("mcvmcomputers.pc_item_hdd", 
                            stack.getTag().getString("VHDName"))
                            .withStyle(ChatFormatting.GRAY));
                    
                    if(!stack.getTag().getString("ISOName").isEmpty())
                        tooltip.add(Component.translatable("mcvmcomputers.pc_item_iso", 
                            stack.getTag().getString("ISOName"))
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }
    
    @Override
    public Component getName(ItemStack stack) {
        if(stack.getTag() != null) {
            if (stack.getTag().contains("MoboInstalled")) {
                if(stack.getTag().getBoolean("MoboInstalled")) {
                    return Component.translatable("mcvmcomputers.pc_item_built");
                }
            }
        }
        return Component.translatable("item.mcvmcomputers.pc_case");
    }
    
    public static ItemStack createPCStackByEntity(EntityPC pc) {
        ItemStack is = new ItemStack(ItemList.PC_CASE.get());
        if(pc.getMotherboardInstalled()) {
            CompoundTag ct = is.getOrCreateTag();
            ct.putBoolean("x64", pc.get64Bit());
            ct.putBoolean("MoboInstalled", pc.getMotherboardInstalled());
            ct.putBoolean("GPUInstalled", pc.getGpuInstalled());
            ct.putInt("CPUDividedBy", pc.getCpuDividedBy());
            ct.putInt("RAMSlot0", pc.getGigsOfRamInSlot0());
            ct.putInt("RAMSlot1", pc.getGigsOfRamInSlot1());
            ct.putString("VHDName", pc.getHardDriveFileName());
            ct.putString("ISOName", pc.getIsoFileName());
        }
        return is;
    }
}
