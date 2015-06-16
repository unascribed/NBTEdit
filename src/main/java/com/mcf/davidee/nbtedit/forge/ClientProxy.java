package com.mcf.davidee.nbtedit.forge;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.mcf.davidee.nbtedit.NBTEdit;
import com.mcf.davidee.nbtedit.gui.GuiEditNBTTree;
import com.mcf.davidee.nbtedit.nbt.SaveStates;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerInformation(){
		MinecraftForge.EVENT_BUS.register(this);
		SaveStates save = NBTEdit.getSaveStates();
		save.load();
		save.save();
	}

	@Override
	public File getMinecraftDirectory(){
		return FMLClientHandler.instance().getClient().mcDataDir;
	}

	@Override
	public void openEditGUI(final int entityID, final NBTTagCompound tag) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiEditNBTTree(entityID, tag));
			}
		});
	}
	
	@Override
	public void openEditGUI(final int x, final int y, final int z, final NBTTagCompound tag) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiEditNBTTree(x, y, z, tag));
			}
		});
	}

	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event){
		GuiScreen curScreen = Minecraft.getMinecraft().currentScreen;
		if (curScreen instanceof GuiEditNBTTree){
			GuiEditNBTTree screen = (GuiEditNBTTree)curScreen;
			Entity e = screen.getEntity();
			
			if (e != null && e.isEntityAlive())
				drawBoundingBox(event.context, event.partialTicks,e.getBoundingBox());
			else if (screen.isTileEntity()){
				int x = screen.getBlockX();
				int y = screen.y;
				int z = screen.z;
				World world = Minecraft.getMinecraft().theWorld;
				Block b = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (b != null) {
					b.setBlockBoundsBasedOnState(world, new BlockPos(x,y,z));
					drawBoundingBox(event.context,event.partialTicks, b.getSelectedBoundingBox(world,new BlockPos(x, y,z)));
				}
			}
		}
	}

	private void drawBoundingBox(RenderGlobal r, float f, AxisAlignedBB aabb) {
		if (aabb == null)
			return;

		EntityLivingBase player = (EntityLivingBase) Minecraft.getMinecraft().getRenderViewEntity();

		double var8 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)f;
		double var10 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)f;
		double var12 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)f;

		aabb = aabb.expand(-var8, -var10, -var12);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 0.0F, 0.0F, .5F);
		GL11.glLineWidth(3.5F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);

		Tessellator var2 = Tessellator.getInstance();
		WorldRenderer wr = var2.getWorldRenderer();

		wr.startDrawing(3);
		wr.addVertex(aabb.minX, aabb.minY, aabb.minZ);
		wr.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
		wr.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
		wr.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
		wr.addVertex(aabb.minX, aabb.minY, aabb.minZ);
		var2.draw();
		wr.startDrawing(3);
		wr.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
		wr.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
		wr.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
		wr.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
		wr.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
		var2.draw();
		wr.startDrawing(1);
		wr.addVertex(aabb.minX, aabb.minY, aabb.minZ);
		wr.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
		wr.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
		wr.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
		wr.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
		wr.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
		wr.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
		wr.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
		var2.draw();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);

	}
}
