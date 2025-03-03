package mekanism.common.block.prefab;

import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockBasicMultiblock<TILE extends TileEntityMekanism> extends BlockTile<TILE, BlockTypeTile<TILE>> {

    public BlockBasicMultiblock(BlockTypeTile<TILE> type) {
        this(type, BlockBehaviour.Properties.of().strength(5, 9).requiresCorrectToolForDrops().mapColor(MapColor.METAL));
    }

    public BlockBasicMultiblock(BlockTypeTile<TILE> type, BlockBehaviour.Properties properties) {
        super(type, properties);
    }

    @NotNull
    @Override
    @Deprecated
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
          @NotNull BlockHitResult hit) {
        TileEntityMultiblock<?> tile = WorldUtils.getTileEntity(TileEntityMultiblock.class, world, pos);
        if (tile == null) {
            return InteractionResult.PASS;
        } else if (world.isClientSide) {
            if (!MekanismUtils.canUseAsWrench(player.getItemInHand(hand))) {
                if (!tile.hasGui() || !tile.getMultiblock().isFormed()) {
                    //If the block doesn't have a gui (frames of things like the evaporation plant), or the multiblock is not formed then pass
                    return InteractionResult.PASS;
                }
            }
            return InteractionResult.SUCCESS;
        } else if (tile.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return InteractionResult.SUCCESS;
        }
        return tile.onActivate(player, hand, player.getItemInHand(hand));
    }
}