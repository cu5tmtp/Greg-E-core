package net.cu5tmtp.GregECore.wandOfPuppetry;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import static net.cu5tmtp.GregECore.wandOfPuppetry.ReanimationLogic.tryReanimate;

public class WandOfPuppetry extends Item {

    public WandOfPuppetry(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return tryReanimate(context);
    }
}
