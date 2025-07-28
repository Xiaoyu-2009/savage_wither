package savage.wither;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import savage.wither.config.SavageWitherConfig;
import savage.wither.event.WitherEventHandler;

@Mod(SavageWitherMod.MODID)
public class SavageWitherMod {
    public static final String MODID = "savage_wither";

    public SavageWitherMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SavageWitherConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(new WitherEventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}