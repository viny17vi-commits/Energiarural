package com.energiarural;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRegistry {
    private ModRegistry() {
    }

    private static final String MODID = EnergiaRural.MODID;
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = COMPONENTS.registerComponentType("energy",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    // Blocos
    public static final DeferredBlock<WaterMillBlock> WATER_MILL = BLOCKS.registerBlock("water_mill", WaterMillBlock::new,
            p -> p.strength(2.5F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<CableBlock> CABLE = BLOCKS.registerBlock("cable", CableBlock::new,
            p -> p.strength(1.0F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<BatteryBlock> BATTERY = BLOCKS.registerBlock("battery", BatteryBlock::new,
            p -> p.strength(3.0F).sound(SoundType.METAL));
    public static final DeferredBlock<HarvesterBlock> HARVESTER = BLOCKS.registerBlock("harvester", HarvesterBlock::new,
            p -> p.strength(3.0F).sound(SoundType.METAL));

    public static final DeferredItem<?> WATER_MILL_ITEM = ITEMS.registerSimpleBlockItem(WATER_MILL);
    public static final DeferredItem<?> CABLE_ITEM = ITEMS.registerSimpleBlockItem(CABLE);
    public static final DeferredItem<?> BATTERY_ITEM = ITEMS.registerSimpleBlockItem(BATTERY);
    public static final DeferredItem<?> HARVESTER_ITEM = ITEMS.registerSimpleBlockItem(HARVESTER);

    // Block entities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterMillBE>> WATER_MILL_BE = BLOCK_ENTITIES.register("water_mill",
            () -> new BlockEntityType<>(WaterMillBE::new, WATER_MILL.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBE>> CABLE_BE = BLOCK_ENTITIES.register("cable",
            () -> new BlockEntityType<>(CableBE::new, CABLE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBE>> BATTERY_BE = BLOCK_ENTITIES.register("battery",
            () -> new BlockEntityType<>(BatteryBE::new, BATTERY.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HarvesterBE>> HARVESTER_BE = BLOCK_ENTITIES.register("harvester",
            () -> new BlockEntityType<>(HarvesterBE::new, HARVESTER.get()));

    // Armas
    public static final DeferredItem<?>[] WEAPONS = new DeferredItem<?>[WeaponTier.values().length];

    static {
        for (WeaponTier tier : WeaponTier.values()) {
            WEAPONS[tier.ordinal()] = ITEMS.registerItem(tier.id, props -> new EnergyWeaponItem(props.stacksTo(1).attributes(
                    ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, tier.baseDamage - 1.0, AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, tier.speed, AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build()), tier));
        }
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(WATER_MILL_ITEM.get()))
            .displayItems((params, output) -> {
                output.accept(WATER_MILL_ITEM.get());
                output.accept(CABLE_ITEM.get());
                output.accept(BATTERY_ITEM.get());
                output.accept(HARVESTER_ITEM.get());
                for (DeferredItem<?> weapon : WEAPONS) {
                    output.accept(weapon.get());
                }
            })
            .build());

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        COMPONENTS.register(bus);
        TABS.register(bus);
    }

    /** Expõe a energia no formato padrão do NeoForge, o mesmo que o Mekanism usa: cabos do Mekanism conectam direto. */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Energy.BLOCK, WATER_MILL_BE.get(), (be, side) -> be.buffer);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, CABLE_BE.get(), (be, side) -> be.buffer);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, BATTERY_BE.get(), (be, side) -> be.buffer);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, HARVESTER_BE.get(), (be, side) -> be.buffer);
    }
}
