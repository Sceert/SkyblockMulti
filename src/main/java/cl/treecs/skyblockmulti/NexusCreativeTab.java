package cl.treecs.skyblockmulti;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

/** Creative inventory access to every custom recipe output used by the Nexus. */
public final class NexusCreativeTab {
    private NexusCreativeTab() {
    }

    public static void register() {
        CreativeModeTab tab = FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.skyblockmulti"))
                .icon(NexusCreativeTab::earthOffering)
                .displayItems((parameters, output) -> {
                    output.accept(lavaCatalyst());
                    output.accept(offering("earth"));
                    output.accept(offering("trees"));
                    output.accept(offering("metals"));
                    output.accept(offering("war"));
                })
                .build();

        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                Identifier.parse(SkyblockMultiMod.MOD_ID + ":crafting"),
                tab
        );
    }

    private static ItemStack earthOffering() {
        return offering("earth");
    }

    private static ItemStack offering(String type) {
        ItemStack stack = new ItemStack(Items.ECHO_SHARD);
        stack.set(DataComponents.CUSTOM_NAME,
                Component.translatable("skyblockmulti.offering." + type).withStyle(style -> style.withItalic(false)));
        CompoundTag root = new CompoundTag();
        CompoundTag modData = new CompoundTag();
        modData.putString("offering", type);
        root.put("skyblockmulti", modData);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        stack.set(DataComponents.MAX_STACK_SIZE, 1);
        return stack;
    }

    private static ItemStack lavaCatalyst() {
        ItemStack stack = new ItemStack(Items.FIRE_CHARGE);
        stack.set(DataComponents.CUSTOM_NAME,
                Component.translatable("skyblockmulti.lava_catalyst").withStyle(style -> style.withItalic(false)));
        CompoundTag root = new CompoundTag();
        CompoundTag modData = new CompoundTag();
        modData.putBoolean("lava_catalyst", true);
        root.put("skyblockmulti", modData);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        stack.set(DataComponents.MAX_STACK_SIZE, 1);
        return stack;
    }
}
