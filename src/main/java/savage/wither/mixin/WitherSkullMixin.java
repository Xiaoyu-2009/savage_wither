package savage.wither.mixin;

import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import savage.wither.config.SavageWitherConfig;

@Mixin(AbstractHurtingProjectile.class)
public class WitherSkullMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void maintainHighSpeedAndTracking(CallbackInfo ci) {
        if (!(((Object) this) instanceof WitherSkull)) {
            return;
        }
        WitherSkull skull = (WitherSkull) (Object) this;

        if (!SavageWitherConfig.ENABLE_PREDICTIVE_AIM.get()) {
            return;
        }

        Vec3 currentMotion = skull.getDeltaMovement();

        if (currentMotion.lengthSqr() > 0.001) {
            double speedMultiplier;

            if (skull.isDangerous()) {
                speedMultiplier = SavageWitherConfig.BLUE_SKULL_SPEED_MULTIPLIER.get();
            } else {
                speedMultiplier = SavageWitherConfig.BLACK_SKULL_SPEED_MULTIPLIER.get();
            }

            Vec3 adjustedMotion = applySmartTracking(skull, currentMotion, speedMultiplier);

            double currentSpeed = adjustedMotion.length();
            double expectedSpeed = 0.5 * speedMultiplier;

            if (currentSpeed < expectedSpeed) {
                Vec3 direction = adjustedMotion.normalize();
                adjustedMotion = direction.scale(expectedSpeed);
            }

            skull.setDeltaMovement(adjustedMotion);
        }
    }

    private Vec3 applySmartTracking(WitherSkull skull, Vec3 currentMotion, double speedMultiplier) {
        net.minecraft.world.entity.LivingEntity target = skull.level().getNearestPlayer(
                skull.getX(), skull.getY(), skull.getZ(), 32.0, false);

        if (target == null) {
            java.util.List<net.minecraft.world.entity.LivingEntity> entities = skull.level().getEntitiesOfClass(
                    net.minecraft.world.entity.LivingEntity.class,
                    skull.getBoundingBox().inflate(16.0),
                    entity -> entity != skull.getOwner() && entity.isAlive());

            if (!entities.isEmpty()) {
                target = entities.get(0);
            }
        }

        if (target != null) {
            Vec3 skullPos = skull.position();
            Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
            Vec3 targetVelocity = target.getDeltaMovement();

            double distance = skullPos.distanceTo(targetPos);

            if (distance < 20.0 && distance > 1.0) {
                double flightTime = distance / (currentMotion.length() + 0.1);
                Vec3 predictedTargetPos = targetPos.add(targetVelocity.scale(flightTime));

                Vec3 idealDirection = predictedTargetPos.subtract(skullPos).normalize();
                Vec3 currentDirection = currentMotion.normalize();

                double adjustmentStrength = Math.max(0.1, 1.0 - (distance / 20.0));
                adjustmentStrength *= 0.15;

                Vec3 adjustedDirection = currentDirection.lerp(idealDirection, adjustmentStrength);

                return adjustedDirection.scale(currentMotion.length());
            }
        }

        return currentMotion;
    }
}