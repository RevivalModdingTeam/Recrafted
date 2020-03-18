package dev.revivalmoddingteam.recrafted.client.render;

import dev.revivalmoddingteam.recrafted.common.entity.RecraftedItemEntity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientManager {

    public static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(RecraftedItemEntity.class, RecraftedItemRenderer::new);
    }
}
