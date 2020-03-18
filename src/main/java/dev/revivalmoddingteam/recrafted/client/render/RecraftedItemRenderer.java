package dev.revivalmoddingteam.recrafted.client.render;

import dev.revivalmoddingteam.recrafted.common.entity.RecraftedItemEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RecraftedItemRenderer extends EntityRenderer<RecraftedItemEntity> {

    public RecraftedItemRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(RecraftedItemEntity entity) {
        return new ResourceLocation("");
    }
}
