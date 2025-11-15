package mcvmcomputers.item;

import mcvmcomputers.MainMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemHarddrive extends OrderableItem {
    public ItemHarddrive(Properties properties) {
        super(properties, 6);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide) {
            MainMod.hardDriveClick.run();
        }
        return super.use(level, player, hand);
    }
    
    @Override
    public Component getName(ItemStack stack) {
        if(stack.getTag() != null) {
            if(stack.getTag().contains("vhdfile")) {
                return Component.translatable("mcvmcomputers.hdd_item_name", stack.getTag().getString("vhdfile"));
            }
        }
        return Component.translatable("mcvmcomputers.hdd_item_name", 
            Component.translatable("mcvmcomputers.hdd_right_click").getString());
    }
    
    public static ItemStack createHardDrive(String fileName) {
        ItemStack is = new ItemStack(ItemList.ITEM_HARDDRIVE.get());
        CompoundTag ct = is.getOrCreateTag();
        ct.putString("vhdfile", fileName);
        is.setTag(ct);
        return is;
    }
}
