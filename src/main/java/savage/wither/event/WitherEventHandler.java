package savage.wither.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import savage.wither.config.SavageWitherConfig;

public class WitherEventHandler {

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof WitherBoss wither) {
            enhanceWither(wither);
        }

        if (event.getEntity() instanceof WitherSkull skull) {
            enhanceWitherSkull(skull);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof WitherBoss) {
            float damage = event.getAmount();
            float multiplier = SavageWitherConfig.WITHER_DAMAGE_MULTIPLIER.get().floatValue();
            event.setAmount(damage * multiplier);
        }

        if (event.getSource().getEntity() instanceof WitherSkull witherSkull) {
            float damage = event.getAmount();
            float multiplier;

            if (witherSkull.isDangerous()) {
                multiplier = SavageWitherConfig.BLUE_SKULL_DAMAGE_MULTIPLIER.get().floatValue();
            } else {
                multiplier = SavageWitherConfig.BLACK_SKULL_DAMAGE_MULTIPLIER.get().floatValue();
            }

            event.setAmount(damage * multiplier);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.getServer().getAllLevels().forEach(level -> {
                level.getEntities().getAll().forEach(entity -> {
                    if (entity instanceof WitherSkull skull) {
                        Vec3 currentMotion = skull.getDeltaMovement();
                        if (currentMotion.lengthSqr() > 0.001) {
                            double speedMultiplier;

                            if (skull.isDangerous()) {
                                speedMultiplier = SavageWitherConfig.BLUE_SKULL_SPEED_MULTIPLIER.get();
                            } else {
                                speedMultiplier = SavageWitherConfig.BLACK_SKULL_SPEED_MULTIPLIER.get();
                            }

                            double currentSpeed = currentMotion.length();
                            double minSpeed = 1.0 * speedMultiplier;

                            if (currentSpeed < minSpeed) {
                                Vec3 direction = currentMotion.normalize();
                                skull.setDeltaMovement(direction.scale(minSpeed));
                            }
                        }
                    }

                    if (entity instanceof WitherBoss wither) {
                        if (SavageWitherConfig.ENABLE_PREDICTIVE_AIM.get()) {
                            int frequency = SavageWitherConfig.PREDICTIVE_AIM_FREQUENCY.get();
                            if (wither.tickCount % frequency == 0) {
                                performPredictiveAttack(wither);
                            }
                        }
                    }
                });
            });
        }
    }

    private void enhanceWither(WitherBoss wither) {
        try {
            if (wither.getAttribute(Attributes.MAX_HEALTH) != null) {
                double baseHealth = wither.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                double newHealth = baseHealth * SavageWitherConfig.WITHER_HEALTH_MULTIPLIER.get();
                wither.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
                wither.setHealth((float) newHealth);
            }

            if (wither.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                double baseSpeed = wither.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
                double newSpeed = baseSpeed * SavageWitherConfig.WITHER_SPEED_MULTIPLIER.get();
                wither.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);
            }

            if (wither.getAttribute(Attributes.ARMOR) != null) {
                double baseArmor = wither.getAttribute(Attributes.ARMOR).getBaseValue();
                double newArmor = baseArmor * SavageWitherConfig.WITHER_ARMOR_MULTIPLIER.get();
                wither.getAttribute(Attributes.ARMOR).setBaseValue(newArmor);
            }

            if (wither.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                double baseDamage = wither.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                double newDamage = baseDamage * SavageWitherConfig.WITHER_DAMAGE_MULTIPLIER.get();
                wither.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newDamage);
            }
        } catch (Exception e) {}
    }

    private void enhanceWitherSkull(WitherSkull skull) {
        Vec3 currentMotion = skull.getDeltaMovement();
        if (currentMotion.lengthSqr() > 0.001) {
            double speedMultiplier;
            if (skull.isDangerous()) {
                speedMultiplier = SavageWitherConfig.BLUE_SKULL_SPEED_MULTIPLIER.get();
            } else {
                speedMultiplier = SavageWitherConfig.BLACK_SKULL_SPEED_MULTIPLIER.get();
            }
            Vec3 enhancedMotion = currentMotion.scale(speedMultiplier);
            skull.setDeltaMovement(enhancedMotion);
        }
    }

    private void performPredictiveAttack(WitherBoss wither) {
        LivingEntity target = wither.getTarget();

        if (target != null && !wither.level().isClientSide) {
            Vec3 predictedPos = calculatePredictiveAim(wither, target);
            Vec3 witherPos = wither.position().add(0, wither.getBbHeight() * 0.5, 0);
            Vec3 direction = predictedPos.subtract(witherPos).normalize();

            boolean isDangerous = wither.getRandom().nextFloat() < 0.3f;
            double speedMultiplier = isDangerous
                    ? SavageWitherConfig.BLUE_SKULL_SPEED_MULTIPLIER.get()
                    : SavageWitherConfig.BLACK_SKULL_SPEED_MULTIPLIER.get();

            double baseSpeed = 3.0;
            Vec3 velocity = direction.scale(baseSpeed * speedMultiplier);

            WitherSkull skull = new WitherSkull(wither.level(), wither, velocity.x, velocity.y, velocity.z);
            skull.setDangerous(isDangerous);
            skull.setPos(witherPos.x, witherPos.y, witherPos.z);

            wither.level().addFreshEntity(skull);
        }
    }

    private Vec3 calculatePredictiveAim(WitherBoss wither, LivingEntity target) {
        Vec3 witherPos = wither.position().add(0, wither.getBbHeight() * 0.5, 0);
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 targetVelocity = target.getDeltaMovement();

        double accuracy = SavageWitherConfig.PREDICTIVE_AIM_ACCURACY.get();
        double skullSpeed = 3.0 * SavageWitherConfig.BLUE_SKULL_SPEED_MULTIPLIER.get();
        double distance = witherPos.distanceTo(targetPos);

        double flightTime = distance / skullSpeed;
        double accuracyFactor = 0.8 + (accuracy * 0.4);
        flightTime *= accuracyFactor;

        Vec3 predictedPos = targetPos.add(targetVelocity.scale(flightTime));
        Vec3 targetLookDirection = target.getLookAngle();
        double targetSpeed = targetVelocity.length();

        double frontDistance = (1.0 + targetSpeed * 2.0) * accuracy;
        Vec3 frontPosition = predictedPos.add(targetLookDirection.scale(frontDistance));

        if (targetSpeed > 0.1) {
            Vec3 sideDirection = targetLookDirection.cross(new Vec3(0, 1, 0)).normalize();
            double sideOffset = (wither.getRandom().nextDouble() - 0.5) * targetSpeed * 3.0 * (2.0 - accuracy);
            frontPosition = frontPosition.add(sideDirection.scale(sideOffset));
        }

        double randomFactor = 2.0 - accuracy;
        Vec3 randomOffset = new Vec3(
        (wither.getRandom().nextDouble() - 0.5) * 2.0 * randomFactor,
        (wither.getRandom().nextDouble() - 0.5) * 1.0 * randomFactor,
        (wither.getRandom().nextDouble() - 0.5) * 2.0 * randomFactor);

        return frontPosition.add(randomOffset);
    }
}