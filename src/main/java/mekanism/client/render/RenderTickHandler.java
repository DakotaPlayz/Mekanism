package mekanism.client.render;

import java.util.Random;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.MekanismAPI;
import mekanism.api.Pos3D;
import mekanism.client.render.particle.EntityJetpackFlameFX;
import mekanism.client.render.particle.EntityJetpackSmokeFX;
import mekanism.client.render.particle.EntityScubaBubbleFX;
import mekanism.common.Mekanism;
import mekanism.common.item.ItemFlamethrower;
import mekanism.common.item.ItemJetpack;
import mekanism.common.item.ItemScubaTank;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTickHandler
{
	public Random rand = new Random();
	public Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void tickEnd(RenderTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			if(mc.thePlayer != null && mc.theWorld != null && !mc.isGamePaused())
			{
				EntityPlayer player = mc.thePlayer;
				World world = mc.thePlayer.worldObj;
	
				FontRenderer font = mc.fontRendererObj;
	
				MovingObjectPosition pos = player.rayTrace(40.0D, 1.0F);
	
				if(pos != null)
				{	
					Coord4D obj = new Coord4D(pos.getBlockPos(), world.provider.getDimensionId());
	
					if(MekanismAPI.debug && mc.currentScreen == null && !mc.gameSettings.showDebugInfo)
					{
						String tileDisplay = "";
	
						if(obj.getTileEntity(world) != null)
						{
							if(obj.getTileEntity(world).getClass() != null)
							{
								tileDisplay = obj.getTileEntity(world).getClass().getSimpleName();
							}
						}
	
						font.drawStringWithShadow("Block: " + obj.getBlock(world).getUnlocalizedName(), 1, 1, 0x404040);
						font.drawStringWithShadow("Metadata: " + obj.getBlockState(world), 1, 10, 0x404040);
						font.drawStringWithShadow("Location: " + MekanismUtils.getCoordDisplay(obj), 1, 19, 0x404040);
						font.drawStringWithShadow("TileEntity: " + tileDisplay, 1, 28, 0x404040);
						font.drawStringWithShadow("Side: " + pos.sideHit, 1, 37, 0x404040);
					}
				}
	
				if(player != null && mc.currentScreen == null && player.getEquipmentInSlot(3) != null)
				{
					ItemStack stack = player.getEquipmentInSlot(3);
	
					ScaledResolution scaledresolution = new ScaledResolution(mc);
	
					int x = scaledresolution.getScaledWidth();
					int y = scaledresolution.getScaledHeight();
	
					if(stack.getItem() instanceof ItemJetpack)
					{
						ItemJetpack jetpack = (ItemJetpack)stack.getItem();
	
						font.drawStringWithShadow("Mode: " + jetpack.getMode(stack).getName(), 1, y - 20, 0x404040);
						font.drawStringWithShadow("Hydrogen: " + jetpack.getStored(stack), 1, y - 11, 0x404040);
					}
					else if(stack.getItem() instanceof ItemScubaTank)
					{
						ItemScubaTank scubaTank = (ItemScubaTank)stack.getItem();
						String state = (scubaTank.getFlowing(stack) ? EnumColor.DARK_GREEN + "On" : EnumColor.DARK_RED + "Off");
	
						font.drawStringWithShadow("Mode: " + state, 1, y - 20, 0x404040);
						font.drawStringWithShadow("Oxygen: " + scubaTank.getStored(stack), 1, y - 11, 0x404040);
					}
				}

				synchronized(Mekanism.jetpackOn)
				{
					for(String s : Mekanism.jetpackOn)
					{
						EntityPlayer p = mc.theWorld.getPlayerEntityByName(s);

						if(p == null)
						{
							continue;
						}

						Pos3D playerPos = new Pos3D(p).translate(0, 1.7, 0);

						float random = (rand.nextFloat() - 0.5F) * 0.1F;

						Pos3D vLeft = new Pos3D(-0.43, -0.55, -0.54).rotatePitch(p.isSneaking() ? 25 : 0).rotateYaw(p.renderYawOffset);
						Pos3D vRight = new Pos3D(0.43, -0.55, -0.54).rotatePitch(p.isSneaking() ? 25 : 0).rotateYaw(p.renderYawOffset);
						Pos3D vCenter = new Pos3D((rand.nextFloat() - 0.5F) * 0.4F, -0.86, -0.30).rotatePitch(p.isSneaking() ? 25 : 0).rotateYaw(p.renderYawOffset);

						Pos3D rLeft = vLeft.scale(random);
						Pos3D rRight = vRight.scale(random);

						Pos3D mLeft = vLeft.scale(0.2).translate(new Pos3D(p.motionX, p.motionY, p.motionZ));
						Pos3D mRight = vRight.scale(0.2).translate(new Pos3D(p.motionX, p.motionY, p.motionZ));
						Pos3D mCenter = vCenter.scale(0.2).translate(new Pos3D(p.motionX, p.motionY, p.motionZ));

						mLeft = mLeft.translate(rLeft);
						mRight = mRight.translate(rRight);

						Pos3D v = playerPos.translate(vLeft);
						spawnAndSetParticle(EnumParticleTypes.FLAME, world, v.xCoord, v.yCoord, v.zCoord, mLeft.xCoord, mLeft.yCoord, mLeft.zCoord);
						spawnAndSetParticle(EnumParticleTypes.SMOKE_NORMAL, world, v.xCoord, v.yCoord, v.zCoord, mLeft.xCoord, mLeft.yCoord, mLeft.zCoord);

						v = playerPos.translate(vRight);
						spawnAndSetParticle(EnumParticleTypes.FLAME, world, v.xCoord, v.yCoord, v.zCoord, mRight.xCoord, mRight.yCoord, mRight.zCoord);
						spawnAndSetParticle(EnumParticleTypes.SMOKE_NORMAL, world, v.xCoord, v.yCoord, v.zCoord, mRight.xCoord, mRight.yCoord, mRight.zCoord);

						v = playerPos.translate(vCenter);
						spawnAndSetParticle(EnumParticleTypes.FLAME, world, v.xCoord, v.yCoord, v.zCoord, mCenter.xCoord, mCenter.yCoord, mCenter.zCoord);
						spawnAndSetParticle(EnumParticleTypes.SMOKE_NORMAL, world, v.xCoord, v.yCoord, v.zCoord, mCenter.xCoord, mCenter.yCoord, mCenter.zCoord);
					}
				}
				
				synchronized(Mekanism.gasmaskOn)
				{
					if(world.getWorldTime() % 4 == 0)
					{
						for(String s : Mekanism.gasmaskOn)
						{
							EntityPlayer p = mc.theWorld.getPlayerEntityByName(s);
	
							if(p == null || !p.isInWater())
							{
								continue;
							}
							
							Pos3D playerPos = new Pos3D(p);

							if(p != mc.thePlayer)
							{
								playerPos = playerPos.translate(0, 1.7, 0);
							}
							
							float xRand = (rand.nextFloat() - 0.5F) * 0.08F;
							float yRand = (rand.nextFloat() - 0.5F) * 0.05F;
							
							Pos3D vec = new Pos3D(0.4, 0.4, 0.4).multiply(new Pos3D(p.getLook(90))).translate(0, -0.2, 0);
							Pos3D motion = vec.scale(0.2).translate(new Pos3D(p.motionX, p.motionY, p.motionZ));
							
							Pos3D v = playerPos.translate(vec);
							spawnAndSetParticle(EnumParticleTypes.WATER_BUBBLE, world, v.xCoord, v.yCoord, v.zCoord, motion.xCoord, motion.yCoord + 0.2, motion.zCoord);
						}
					}
				}
				
				if(world.getWorldTime() % 4 == 0)
				{
					for(EntityPlayer p : world.playerEntities)
					{
						if(!Mekanism.flamethrowerActive.contains(p.getName()) && !p.isSwingInProgress && p.getCurrentEquippedItem() != null && p.getCurrentEquippedItem().getItem() instanceof ItemFlamethrower)
						{
							if(((ItemFlamethrower)p.getCurrentEquippedItem().getItem()).getGas(p.getCurrentEquippedItem()) != null)
							{
								Pos3D playerPos = new Pos3D(p);
								Pos3D flameVec;

								double flameXCoord = 0;
								double flameYCoord = 0;
								double flameZCoord = 0;
								
								if(p.isSneaking())
								{
									flameYCoord -= 0.35F;
									flameZCoord -= 0.15F;
								}
								
								Pos3D flameMotion = new Pos3D(p.motionX, p.onGround ? 0 : p.motionY, p.motionZ);
								
								if(player == p && mc.gameSettings.thirdPersonView == 0)
								{
									flameVec = new Pos3D(0.8, 0.8, 0.8).multiply(new Pos3D(p.getLook(90))).rotateYaw(15);
								}
								else {
									flameXCoord -= 0.45F;
									
									if(player == p)
									{
										flameYCoord -= 0.5F;
									}
									else {
										flameYCoord += 1F;
									}
									
									flameZCoord += 1.05F;
									
									flameVec = new Pos3D(flameXCoord, flameYCoord, flameZCoord).rotateYaw(p.renderYawOffset);
								}
								
								Pos3D mergedVec = playerPos.translate(flameVec);
								
								spawnAndSetParticle(EnumParticleTypes.FLAME, world, mergedVec.xCoord, mergedVec.yCoord, mergedVec.zCoord, flameMotion.xCoord, flameMotion.yCoord, flameMotion.zCoord);
							}
						}
					}
				}
			}
		}
	}

	public void spawnAndSetParticle(EnumParticleTypes s, World world, double x, double y, double z, double velX, double velY, double velZ)
	{
		EntityFX fx = null;

		if(s.equals(EnumParticleTypes.FLAME))
		{
			fx = new EntityJetpackFlameFX(world, x, y, z, velX, velY, velZ);
		}
		else if(s.equals(EnumParticleTypes.SMOKE_NORMAL))
		{
			fx = new EntityJetpackSmokeFX(world, x, y, z, velX, velY, velZ);
		}
		else if(s.equals("bubble"))
		{
			fx = new EntityScubaBubbleFX(world, x, y, z, velX, velY, velZ);
		}

		mc.effectRenderer.addEffect(fx);
	}
}
