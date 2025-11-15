package mcvmcomputers.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemPackage extends Item {
    public ItemPackage(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide) {
            ItemStack is = player.getItemInHand(hand);
            if(is.getTag() != null) {
                if(is.getTag().contains("packaged_item")) {
                    is.shrink(1);
                    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(is.getTag().getString("packaged_item")));
                    player.addItem(new ItemStack(item));
                }
            }
        }
        return super.use(level, player, hand);
    }
    
    @Override
    public Component getName(ItemStack stack) {
        if(stack.getTag() != null) {
            if(stack.getTag().contains("packaged_item")) {
                Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(stack.getTag().getString("packaged_item")));
                return Component.translatable("mcvmcomputers.packaged")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(item.getDescriptionId())
                        .withStyle(ChatFormatting.GREEN));
            }
        }
        return Component.translatable("mcvmcomputers.invalid_package").withStyle(ChatFormatting.RED);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("mcvmcomputers.open_with_right_click")
            .withStyle(ChatFormatting.GRAY));
    }
    
    public static ItemStack createPackage(ResourceLocation id) {
        ItemStack is = new ItemStack(ItemList.ITEM_PACKAGE.get());
        is.getOrCreateTag().putString("packaged_item", id.toString());
        return is;
    }
}
