package dev.revivalmoddingteam.recrafted.world.feature.tree;

import dev.revivalmoddingteam.recrafted.handler.Registry;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class AppleTree extends Tree {

    @Nullable
    @Override
    protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
        return Registry.RFeatures.APPLE_TREE_FEATURE.get();
    }
}
