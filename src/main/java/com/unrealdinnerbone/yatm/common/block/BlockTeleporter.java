package com.unrealdinnerbone.yatm.common.block;

import com.unrealdinnerbone.yatm.Yatm;
import com.unrealdinnerbone.yatm.lib.DimBlockPos;
import com.unrealdinnerbone.yatm.packet.PacketHandler;
import com.unrealdinnerbone.yatm.packet.open.PacketOpenSetFrequencyGUI;
import com.unrealdinnerbone.yatm.world.YatmWorldSaveData;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTeleporter extends Block implements ITileEntityProvider {

    private final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);

    public BlockTeleporter() {
        super(Material.GROUND, MapColor.CYAN);
        setUnlocalizedName("teleporter");
        this.fullBlock = false;
        this.setLightOpacity(255);

    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTeleporter();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            TileEntityTeleporter tileEntityTeleporter = (TileEntityTeleporter) tileEntity;
            if (playerIn instanceof EntityPlayerMP) {
                YatmWorldSaveData saveData = YatmWorldSaveData.get(worldIn);
                DimBlockPos dimBlockPos = new DimBlockPos(pos, worldIn.provider.getDimension());
                PacketHandler.INSTANCE.sendTo(new PacketOpenSetFrequencyGUI(dimBlockPos, saveData.getIDFormPos(dimBlockPos), tileEntityTeleporter.getFrequencyEffect()), (EntityPlayerMP) playerIn);
            }
        }
        return true;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        YatmWorldSaveData.get(worldIn).removeDimBlockPos(new DimBlockPos(pos, worldIn.provider.getDimension()));
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof EntityPlayer) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            TileEntityTeleporter tileEntityTeleporter = (TileEntityTeleporter) tileEntity;
            tileEntityTeleporter.onEntityWalk((EntityPlayer) entityIn);
        }
    }

}
