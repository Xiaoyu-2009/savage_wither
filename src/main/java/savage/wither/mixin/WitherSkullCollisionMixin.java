package savage.wither.mixin;

import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkull.class)
public class WitherSkullCollisionMixin {
    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void preventSkullCollision(HitResult hitResult, CallbackInfo ci) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) hitResult;
            if (entityHit.getEntity() instanceof WitherSkull) {
                ci.cancel();
                return;
            }
        }
    }
}