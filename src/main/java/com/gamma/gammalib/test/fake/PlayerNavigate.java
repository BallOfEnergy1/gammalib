package com.gamma.gammalib.test.fake;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PlayerNavigate {

    private final TrueFakePlayer player;
    private final World worldObj;
    private PathEntity currentPath;
    private int totalTicks;
    private int ticksAtLastPos;
    private final Vec3 lastPosCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

    public PlayerNavigate(TrueFakePlayer player, World world) {
        this.player = player;
        this.worldObj = world;
    }

    public void setPath(PathEntity path) {
        if (path == null) {
            this.currentPath = null;
            return;
        }

        if (!path.isSamePath(this.currentPath)) {
            this.currentPath = path;
        }

        if (this.currentPath.getCurrentPathLength() != 0) {
            Vec3 vec3 = this.getEntityPosition();
            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck.xCoord = vec3.xCoord;
            this.lastPosCheck.yCoord = vec3.yCoord;
            this.lastPosCheck.zCoord = vec3.zCoord;
        }
    }

    public void onUpdateNavigation() {
        ++this.totalTicks;

        if (this.hasPath()) {
            if (this.canNavigate()) {
                this.pathFollow();
            }

            if (this.hasPath()) {
                Vec3 vec3 = this.currentPath.getPosition(this.player);

                if (vec3 != null) {
                    this.player.getMoveHelper()
                        .setMoveTo(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                }
            }
        }
    }

    private void pathFollow() {
        Vec3 vec3 = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j) {
            if (this.currentPath.getPathPointFromIndex(j).yCoord != (int) vec3.yCoord) {
                i = j;
                break;
            }
        }

        float f = this.player.width * this.player.width;
        int k;

        for (k = this.currentPath.getCurrentPathIndex(); k < i; ++k) {
            if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.player, k)) < (double) f) {
                this.currentPath.setCurrentPathIndex(k + 1);
            }
        }

        k = MathHelper.ceiling_float_int(this.player.width);
        int l = (int) this.player.height + 1;
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1) {
            if (this.isDirectPathBetweenPoints(vec3, this.currentPath.getVectorFromIndex(this.player, j1), k, l, i1)) {
                this.currentPath.setCurrentPathIndex(j1);
                break;
            }
        }

        if (this.totalTicks - this.ticksAtLastPos > 100) {
            if (vec3.squareDistanceTo(this.lastPosCheck) < 2.25D) {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck.xCoord = vec3.xCoord;
            this.lastPosCheck.yCoord = vec3.yCoord;
            this.lastPosCheck.zCoord = vec3.zCoord;
        }
    }

    public boolean hasPath() {
        return this.currentPath != null && !this.currentPath.isFinished();
    }

    public void clearPathEntity() {
        this.currentPath = null;
    }

    private Vec3 getEntityPosition() {
        return Vec3.createVectorHelper(this.player.posX, this.getPathableYPos(), this.player.posZ);
    }

    private int getPathableYPos() {
        if (this.player.isInWater()) {
            int i = (int) this.player.boundingBox.minY;
            Block block = this.worldObj
                .getBlock(MathHelper.floor_double(this.player.posX), i, MathHelper.floor_double(this.player.posZ));
            int j = 0;

            do {
                if (block != Blocks.flowing_water && block != Blocks.water) {
                    return i;
                }

                ++i;
                block = this.worldObj
                    .getBlock(MathHelper.floor_double(this.player.posX), i, MathHelper.floor_double(this.player.posZ));
                ++j;
            } while (j <= 16);

            return (int) this.player.boundingBox.minY;
        } else {
            return (int) (this.player.boundingBox.minY + 0.5D);
        }
    }

    private boolean canNavigate() {
        return this.player.onGround || isInLiquid();
    }

    private boolean isInLiquid() {
        return this.player.isInWater() || this.player.handleLavaMovement();
    }

    private boolean isDirectPathBetweenPoints(Vec3 p_75493_1_, Vec3 p_75493_2_, int p_75493_3_, int p_75493_4_,
        int p_75493_5_) {
        int l = MathHelper.floor_double(p_75493_1_.xCoord);
        int i1 = MathHelper.floor_double(p_75493_1_.zCoord);
        double d0 = p_75493_2_.xCoord - p_75493_1_.xCoord;
        double d1 = p_75493_2_.zCoord - p_75493_1_.zCoord;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        } else {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 *= d3;
            d1 *= d3;
            p_75493_3_ += 2;
            p_75493_5_ += 2;

            if (!this.isSafeToStandAt(
                l,
                (int) p_75493_1_.yCoord,
                i1,
                p_75493_3_,
                p_75493_4_,
                p_75493_5_,
                p_75493_1_,
                d0,
                d1)) {
                return false;
            } else {
                p_75493_3_ -= 2;
                p_75493_5_ -= 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double) (l) - p_75493_1_.xCoord;
                double d7 = (double) (i1) - p_75493_1_.zCoord;

                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 /= d0;
                d7 /= d1;
                int j1 = d0 < 0.0D ? -1 : 1;
                int k1 = d1 < 0.0D ? -1 : 1;
                int l1 = MathHelper.floor_double(p_75493_2_.xCoord);
                int i2 = MathHelper.floor_double(p_75493_2_.zCoord);
                int j2 = l1 - l;
                int k2 = i2 - i1;

                do {
                    if (j2 * j1 <= 0 && k2 * k1 <= 0) {
                        return true;
                    }

                    if (d6 < d7) {
                        d6 += d4;
                        l += j1;
                        j2 = l1 - l;
                    } else {
                        d7 += d5;
                        i1 += k1;
                        k2 = i2 - i1;
                    }
                } while (this.isSafeToStandAt(
                    l,
                    (int) p_75493_1_.yCoord,
                    i1,
                    p_75493_3_,
                    p_75493_4_,
                    p_75493_5_,
                    p_75493_1_,
                    d0,
                    d1));

                return false;
            }
        }
    }

    private boolean isSafeToStandAt(int p_75483_1_, int p_75483_2_, int p_75483_3_, int p_75483_4_, int p_75483_5_,
        int p_75483_6_, Vec3 p_75483_7_, double p_75483_8_, double p_75483_10_) {
        int k1 = p_75483_1_ - p_75483_4_ / 2;
        int l1 = p_75483_3_ - p_75483_6_ / 2;

        if (!this.isPositionClear(
            k1,
            p_75483_2_,
            l1,
            p_75483_4_,
            p_75483_5_,
            p_75483_6_,
            p_75483_7_,
            p_75483_8_,
            p_75483_10_)) {
            return false;
        } else {
            for (int i2 = k1; i2 < k1 + p_75483_4_; ++i2) {
                for (int j2 = l1; j2 < l1 + p_75483_6_; ++j2) {
                    double d2 = (double) i2 + 0.5D - p_75483_7_.xCoord;
                    double d3 = (double) j2 + 0.5D - p_75483_7_.zCoord;

                    if (d2 * p_75483_8_ + d3 * p_75483_10_ >= 0.0D) {
                        Block block = this.worldObj.getBlock(i2, p_75483_2_ - 1, j2);
                        Material material = block.getMaterial();

                        if (material == Material.air) {
                            return false;
                        }

                        if (material == Material.water && !this.player.isInWater()) {
                            return false;
                        }

                        if (material == Material.lava) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean isPositionClear(int p_75496_1_, int p_75496_2_, int p_75496_3_, int p_75496_4_, int p_75496_5_,
        int p_75496_6_, Vec3 p_75496_7_, double p_75496_8_, double p_75496_10_) {
        for (int k1 = p_75496_1_; k1 < p_75496_1_ + p_75496_4_; ++k1) {
            for (int l1 = p_75496_2_; l1 < p_75496_2_ + p_75496_5_; ++l1) {
                for (int i2 = p_75496_3_; i2 < p_75496_3_ + p_75496_6_; ++i2) {
                    double d2 = (double) k1 + 0.5D - p_75496_7_.xCoord;
                    double d3 = (double) i2 + 0.5D - p_75496_7_.zCoord;

                    if (d2 * p_75496_8_ + d3 * p_75496_10_ >= 0.0D) {
                        Block block = this.worldObj.getBlock(k1, l1, i2);

                        if (!block.getBlocksMovement(this.worldObj, k1, l1, i2)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
