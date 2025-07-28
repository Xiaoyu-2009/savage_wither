package savage.wither.mixin;

import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import savage.wither.config.SavageWitherConfig;

@Mixin(WitherBoss.class)
public class WitherBossMixin {

    @Shadow
    private int[] nextHeadUpdate;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void enhanceAttackRate(CallbackInfo ci) {
        double rateMultiplier = SavageWitherConfig.WITHER_SKULL_SHOOT_RATE_MULTIPLIER.get();
        if (rateMultiplier > 1.0) {
            for (int i = 0; i < nextHeadUpdate.length; i++) {
                if (nextHeadUpdate[i] > 1) {
                    nextHeadUpdate[i] = Math.max(1, (int) (nextHeadUpdate[i] / rateMultiplier));
                }
            }
        }
    }
}