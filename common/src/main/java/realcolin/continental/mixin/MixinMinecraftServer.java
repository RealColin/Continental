package realcolin.continental.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
abstract class MixinMinecraftServer {

    // TODO i still need to mixin this class but I'm not sure how yet so I'm going to keep it

    @Inject(method = "loadLevel", at = @At("HEAD"))
    private void loadPack(CallbackInfo ci) {

    }
}
