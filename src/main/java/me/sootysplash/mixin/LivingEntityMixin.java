package me.sootysplash.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.sootysplash.JumpResetIndicator.*;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(at = @At("HEAD"), method = "jump")
	private void jump(CallbackInfo info) {
		if((Object) this == mc.player) {
			jumpAge = mc.player.age;
			lastModTime = System.currentTimeMillis();
		}
	}
	@Inject(at = @At("HEAD"), method = "onDamaged")
	private void onDamage(CallbackInfo info) {
		if((Object) this == mc.player) {
			hurtAge = mc.player.age;
		}
	}
}