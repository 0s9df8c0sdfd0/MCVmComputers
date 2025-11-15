package mcvmcomputers.item;

import net.minecraft.world.item.Item;

public class OrderableItem extends Item {
    private final int price;
    
    public OrderableItem(Properties properties, int price) {
        super(properties);
        this.price = price;
    }
    
    /**
     * @return Price in Iron Ingots.
     */
    public int getPrice() {
        return price;
    }
}
