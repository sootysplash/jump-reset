package me.sootysplash.JR.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.sootysplash.JR.JumpResetIndicator.*;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(at = @At("HEAD"), method = "jumpFromGround")
	private void jump(CallbackInfo info) {
		if((Object) this == mc.player) {
			jumpAge = mc.player.tickCount;
			lastModTime = System.currentTimeMillis();
		}
	}

	@Inject(at = @At("HEAD"), method = "handleDamageEvent")
	private void onDamage(CallbackInfo info) {
		if((Object) this == mc.player) {
			hurtAge = mc.player.tickCount;
		}
	}

}