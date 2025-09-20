package realcolin.continental.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldCreationUiState.class)
public class MixinWorldCreationUiState {

//    @Inject(method = "setSettings", at = @At("TAIL"))
//    private void afterSettingsUpdate(WorldCreationContext ctx, CallbackInfo ci) {
//        System.out.println("JOE WHATEVER");
//        var mc = Minecraft.getInstance();
//        System.out.println(mc.screen);
//        if (mc.screen instanceof CreateWorldScreen sc) {
//            System.out.println("AAA");
//            ((MixinCreateWorldScreen)(Object)sc).resumeCreate();
//        }
//    }
}
