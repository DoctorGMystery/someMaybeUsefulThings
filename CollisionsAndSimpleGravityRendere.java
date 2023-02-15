// Minecraft Forge 1.18.2
// NOT PERFECT OPTIMISED AND TESTED   USE AT YOUR OWN RISK
// free to use
// no updates
// not perfect documentated

package net.drgmystery.mysticalmod.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.drgmystery.mysticalmod.MysticalMod;
import net.drgmystery.mysticalmod.blocks.entities.ClaimerBlockEntity;
import net.drgmystery.mysticalmod.client.model.ModModelLayers;
import net.drgmystery.mysticalmod.client.model.Test;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MODELRenderer implements BlockEntityRenderer<BLOCKENTITY> {

    public static final Material MODEL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(MAINCLASS.MOD_ID, "PATH/TEXTURE"));
    private final MODEL partModel;
    private AABB[] baseAABB = new AABB[10];         //normal sive of the collsion-box
    private AABB[] localeAABB = new AABB[10];         //normal size + position
    private final float gravity = 0.0005F;          //gravity value

    public ClaimerRenderer(BlockEntityRendererProvider.Context pContext) {
        this.partModel = new MODEL(pContext.bakeLayer(ModModelLayers.MODEL));
    }

    @Override
    public void render(BLOCKENTITY pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        this.baseAABB[0] = new AABB(pixelToCord(6), pixelToCord(11), pixelToCord(6), pixelToCord(10), pixelToCord(11), pixelToCord(10));      //add normal AABB size
        VoxelShape shape = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().below((int)Math.floor(pBlockEntity.getProgress()) * -1)).getShape(pBlockEntity.getLevel(), pBlockEntity.getBlockPos().below((int)Math.floor(pBlockEntity.getProgress()) * -1));       //get shape of block below curent AABB pos (here only as y axis used and saved in the blockentity class)
        AABB belowAABB = !shape.isEmpty() ? shape.bounds() : null;      //get bounds (AABB) of the previous shape
        double oldProgress = pBlockEntity.getProgress();
        boolean intersects = false;

        if (belowAABB != null) {
            belowAABB = AABBWithPosition(belowAABB, pBlockEntity.getBlockPos().below((int)Math.floor(pBlockEntity.getProgress()) * -1));    //adds the position to the AABB of the block below
        }

        pPoseStack.pushPose();

        if (localeAABB[0] != null) {
            intersects = belowAABB != null ? localeAABB[0].intersects(belowAABB) : false;       //checks for collision
        }

        pBlockEntity.setVelocity(intersects ? pBlockEntity.getVelocity() : pBlockEntity.getVelocity() + gravity);     //velocity calculation
        pBlockEntity.setVelocity(intersects ||pBlockEntity.getVelocity() > 0.065F ? 0.065F : pBlockEntity.getVelocity());       //max velocity

        pBlockEntity.setProgress(intersects ? pBlockEntity.getProgress() : pBlockEntity.getProgress() - pBlockEntity.getVelocity());      //progress calculation (y axis)

        pPoseStack.translate(pixelToCord(6), pBlockEntity.getProgress() + 0.0077F, pixelToCord(6));       //adding the progress to the render object's position

        if (!(pBlockEntity.getProgress() - 0.01F <= 0F)) {
            localeAABB[0] = updatingAABB(new Vec3(0, oldProgress - pBlockEntity.getProgress(), 0), pBlockEntity.getBlockPos());       //updates to progress positon 0
        } else {
            localeAABB[0] = updatingAABB(new Vec3(0, oldProgress - pBlockEntity.getProgress(), 0), pBlockEntity.getBlockPos().below((int)Math.floor(pBlockEntity.getProgress()) * -1));       //updating AABB to moved position
        }


            //pPoseStack.scale(1.0F, 1.0F, 1.0F);                           SCALING
            //pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(180));         ROTATION

        VertexConsumer vertexconsumer = ClaimerRenderer.PART_TEXTURE.buffer(pBufferSource, RenderType::entitySolid);
        this.partModel.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }

    public static float pixelToCord(int pixels) {
        return pixels * 0.0625F;      //calculates pixel length to block length
    }

    private AABB AABBWithPosition(AABB base, BlockPos pos) {      //adds a position to a AABB
        double minZ = base.minZ;
        double maxZ = base.maxZ;
        double minX = base.minX;
        double maxX = base.maxX;
        double minY = base.minY;
        double maxY = base.maxY;
        Vec3 min = new Vec3(minX + pos.getX(), minY + pos.getY(), minZ + pos.getZ());
        Vec3 max = new Vec3(maxX + pos.getX(), maxY + pos.getY(), maxZ + pos.getZ());
        return new AABB(min, max);
    }

    private AABB updatingAABB(Vec3 moved, BlockPos pos) {         //updating AABB based on baseAABB to the moved position and global position
        double minZ = baseAABB[0].minZ;
        double maxZ = baseAABB[0].maxZ;
        double minX = baseAABB[0].minX;
        double maxX = baseAABB[0].maxX;
        double minY = baseAABB[0].minY;
        double maxY = baseAABB[0].maxY;
        Vec3 min = new Vec3(minX + pos.getX() + moved.x, minY + pos.getY() + moved.y, minZ + pos.getZ() + moved.y);
        Vec3 max = new Vec3(maxX + pos.getX() + moved.x, maxY + pos.getY() + moved.y, maxZ + pos.getZ() + moved.z);
        return new AABB(min, max);
    }
}
