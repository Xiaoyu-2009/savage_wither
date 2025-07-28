package savage.wither.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import savage.wither.config.SavageWitherConfig;

@Mixin(WitherBoss.class)
public class WitherPredictiveAimMixin {

    @Shadow
    private int[] nextHeadUpdate;

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void addPredictiveAttack(CallbackInfo ci) {
        WitherBoss wither = (WitherBoss) (Object) this;
        LivingEntity target = wither.getTarget();

        if (!SavageWitherConfig.ENABLE_PREDICTIVE_AIM.get()) {
            return;
        }

        int frequency = SavageWitherConfig.PREDICTIVE_AIM_FREQUENCY.get();
        if (target != null && wither.tickCount % frequency == 0) {
            launchPredictiveSkull(wither, target);
        }
    }

    private void launchPredictiveSkull(WitherBoss wither, LivingEntity target) {
        Vec3 predictedPos = calculatePredictiveAim(wither, target);
        Vec3 witherPos = wither.position().add(0, wither.getBbHeight() * 0.5, 0);
        Vec3 direction = predictedPos.subtract(witherPos).normalize();

        boolean isDangerous = wither.getRandom().nextFloat() < 0.9f;
        double speedMultiplier = isDangerous
                ? SavageWitherConfig.BLUE_SKULL_SPEED_MULTIPLIER.get()
                : SavageWitherConfig.BLACK_SKULL_SPEED_MULTIPLIER.get();

        double baseSpeed = 2.0;

        Vec3 velocity = direction.scale(baseSpeed * speedMultiplier);

        WitherSkull skull = new WitherSkull(wither.level(), wither, velocity.x, velocity.y, velocity.z);
        skull.setDangerous(isDangerous);
        skull.setPos(witherPos.x, witherPos.y, witherPos.z);

        wither.level().addFreshEntity(skull);
    }

    private Vec3 calculatePredictiveAim(WitherBoss wither, LivingEntity target) {
        Vec3 witherPos = wither.position().add(0, wither.getBbHeight() * 0.5, 0);
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 targetVelocity = target.getDeltaMovement();

        double skullSpeed = 2.0 * SavageWitherConfig.BLUE_SKULL_SPEED_MULTIPLIER.get();
        double distance = witherPos.distanceTo(targetPos);
        double flightTime = distance / skullSpeed;

        Vec3 predictedPos = targetPos.add(targetVelocity.scale(flightTime));
        Vec3 targetLookDirection = target.getLookAngle();

        double targetSpeed = targetVelocity.length();
        double frontDistance = 1.5 + targetSpeed * 3.0;

        Vec3 frontPosition = predictedPos.add(targetLookDirection.scale(frontDistance));

        if (targetSpeed > 0.2) {
            Vec3 sideDirection = targetLookDirection.cross(new Vec3(0, 1, 0)).normalize();
            double sideOffset = (wither.getRandom().nextDouble() - 0.5) * targetSpeed * 4.0;
            frontPosition = frontPosition.add(sideDirection.scale(sideOffset));
        }

        double accuracy = SavageWitherConfig.PREDICTIVE_AIM_ACCURACY.get();
        double randomSpread = (2.0 - accuracy);

        Vec3 randomOffset = new Vec3(
                (wither.getRandom().nextDouble() - 0.5) * randomSpread,
                (wither.getRandom().nextDouble() - 0.5) * randomSpread * 0.5,
                (wither.getRandom().nextDouble() - 0.5) * randomSpread);

        return frontPosition.add(randomOffset);
    }
}