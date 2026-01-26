package com.gamma.gammalib.test.fake;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class PlayerMoveHelper {

    private final EntityPlayer player;
    private double posX;
    private double posY;
    private double posZ;
    private boolean update;

    public PlayerMoveHelper(EntityPlayer player) {
        this.player = player;
        this.posX = player.posX;
        this.posY = player.posY;
        this.posZ = player.posZ;
    }

    public void setMoveTo(double p_75642_1_, double p_75642_3_, double p_75642_5_) {
        this.posX = p_75642_1_;
        this.posY = p_75642_3_;
        this.posZ = p_75642_5_;
        this.update = true;
    }

    public void onUpdateMoveHelper() {
        this.player.moveForward = 0.0F;

        if (this.update) {
            this.update = false;
            int i = MathHelper.floor_double(this.player.boundingBox.minY + 0.5D);
            double d0 = this.posX - this.player.posX;
            double d1 = this.posZ - this.player.posZ;
            double d2 = this.posY - (double) i;
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 >= 2.500000277905201E-7D) {
                float f = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
                this.player.rotationYaw = this.limitAngle(this.player.rotationYaw, f, 30.0F);
                this.player.setAIMoveSpeed(
                    (float) (this.player.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                        .getAttributeValue()));

                if (d2 > 0.0D && d0 * d0 + d1 * d1 < 1.0D) {
                    this.player.jump();
                }
            }
        }
    }

    private float limitAngle(float angle, float p_75639_2_, float p_75639_3_) {
        float f3 = MathHelper.wrapAngleTo180_float(p_75639_2_ - angle);

        if (f3 > p_75639_3_) {
            f3 = p_75639_3_;
        }

        if (f3 < -p_75639_3_) {
            f3 = -p_75639_3_;
        }

        return angle + f3;
    }
}
