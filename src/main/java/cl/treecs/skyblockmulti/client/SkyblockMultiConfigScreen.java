package cl.treecs.skyblockmulti.client;

import cl.treecs.skyblockmulti.SkyblockMultiMod;
import cl.treecs.skyblockmulti.SkyblockMultiMod.BonusChestMode;
import cl.treecs.skyblockmulti.SkyblockMultiMod.TreeOption;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;

public final class SkyblockMultiConfigScreen extends Screen {
    private final Screen parent;
    private final EnumMap<TreeOption, Boolean> treeStates;
    private final EnumMap<TreeOption, Button> treeButtons = new EnumMap<>(TreeOption.class);
    private EditBox distanceField;
    private Button bonusChestModeButton;
    private BonusChestMode bonusChestMode;
    private Component status = Component.empty();
    private boolean statusVisible;
    private int statusColor = 0xFFAAAAAA;

    public SkyblockMultiConfigScreen(Screen parent) {
        super(Component.translatable("skyblockmulti.config.title"));
        this.parent = parent;
        this.treeStates = SkyblockMultiMod.getConfiguredTreeStates();
        this.bonusChestMode = SkyblockMultiMod.getConfiguredBonusChestMode();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.distanceField = new EditBox(
                this.font, centerX - 75, 52, 150, 20,
                Component.translatable("skyblockmulti.config.distance.field")
        );
        this.distanceField.setMaxLength(6);
        this.distanceField.setValue(Integer.toString(SkyblockMultiMod.getConfiguredDistance()));
        this.distanceField.setResponder(value -> clearStatus());
        this.addRenderableWidget(this.distanceField);

        this.treeButtons.clear();
        TreeOption[] trees = TreeOption.values();
        int startX = centerX - 160;
        int startY = 108;
        for (int i = 0; i < trees.length; i++) {
            TreeOption tree = trees[i];
            int x = startX + (i % 3) * 108;
            int y = startY + (i / 3) * 25;
            Button button = Button.builder(treeLabel(tree), clicked -> toggleTree(tree, clicked))
                    .bounds(x, y, 104, 20).build();
            this.treeButtons.put(tree, button);
            this.addRenderableWidget(button);
        }

        this.bonusChestModeButton = this.addRenderableWidget(
                Button.builder(bonusChestModeLabel(), button -> cycleBonusChestMode())
                        .bounds(centerX - 155, 210, 310, 20).build()
        );

        int footerY = 240;
        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.reset"), button -> resetDefaults())
                .bounds(centerX - 155, footerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.cancel"), button -> this.onClose())
                .bounds(centerX - 50, footerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.save"), button -> save())
                .bounds(centerX + 55, footerY, 100, 20).build());
    }

    private void clearStatus() {
        this.status = Component.empty();
        this.statusVisible = false;
    }

    private void setStatus(String key, int color) {
        this.status = Component.translatable(key);
        this.statusVisible = true;
        this.statusColor = color;
    }

    private void toggleTree(TreeOption tree, Button button) {
        treeStates.put(tree, !Boolean.TRUE.equals(treeStates.get(tree)));
        button.setMessage(treeLabel(tree));
        clearStatus();
    }

    private Component treeLabel(TreeOption tree) {
        boolean enabled = Boolean.TRUE.equals(treeStates.get(tree));
        return Component.translatable(
                "skyblockmulti.config.tree.button",
                Component.translatable("skyblockmulti.config.tree." + tree.configKey()),
                Component.translatable(enabled ? "skyblockmulti.config.yes" : "skyblockmulti.config.no")
        );
    }

    private void cycleBonusChestMode() {
        this.bonusChestMode = this.bonusChestMode.next();
        this.bonusChestModeButton.setMessage(bonusChestModeLabel());
        clearStatus();
    }

    private Component bonusChestModeLabel() {
        return Component.translatable(
                "skyblockmulti.config.bonus_chest.mode.button",
                Component.translatable("skyblockmulti.config.bonus_chest.mode." + bonusChestMode.configKey())
        );
    }

    private void resetDefaults() {
        this.distanceField.setValue(Integer.toString(SkyblockMultiMod.DEFAULT_DISTANCE));
        for (TreeOption tree : TreeOption.values()) {
            treeStates.put(tree, true);
            Button button = treeButtons.get(tree);
            if (button != null) button.setMessage(treeLabel(tree));
        }
        this.bonusChestMode = BonusChestMode.STANDARD;
        if (this.bonusChestModeButton != null) this.bonusChestModeButton.setMessage(bonusChestModeLabel());
        setStatus("skyblockmulti.config.status.defaults", 0xFFFFFF55);
    }

    private int enabledTreeCount() {
        int count = 0;
        for (TreeOption tree : TreeOption.values()) {
            if (Boolean.TRUE.equals(treeStates.get(tree))) count++;
        }
        return count;
    }

    private int previewDistance() {
        try {
            return SkyblockMultiMod.normalizeDistance(Integer.parseInt(this.distanceField.getValue().trim()));
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void save() {
        if (enabledTreeCount() == 0) {
            setStatus("skyblockmulti.config.status.one_tree", 0xFFFF5555);
            return;
        }
        try {
            int requested = Integer.parseInt(this.distanceField.getValue().trim());
            int normalized = SkyblockMultiMod.normalizeDistance(requested);
            if (SkyblockMultiMod.saveConfiguration(normalized, treeStates, bonusChestMode)) {
                this.distanceField.setValue(Integer.toString(normalized));
                setStatus("skyblockmulti.config.status.saved", 0xFF55FF55);
            } else {
                setStatus("skyblockmulti.config.status.save_failed", 0xFFFF5555);
            }
        } catch (NumberFormatException exception) {
            setStatus("skyblockmulti.config.status.number", 0xFFFF5555);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        graphics.centeredText(this.font, this.title, centerX, 18, 0xFFFFFFFF);
        graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.distance.label"), centerX, 37, 0xFFDDDDDD);

        int distance = previewDistance();
        if (distance > 0) {
            int radius = distance * 2;
            graphics.centeredText(this.font,
                    Component.translatable("skyblockmulti.config.capacity", SkyblockMultiMod.getPlayerCapacity(distance), radius),
                    centerX, 80, 0xFF55FFFF);
        } else {
            graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.invalid_distance"), centerX, 80, 0xFFFF5555);
        }

        graphics.centeredText(this.font,
                Component.translatable("skyblockmulti.config.allowed_trees", enabledTreeCount(), TreeOption.values().length),
                centerX, 97, enabledTreeCount() > 0 ? 0xFFDDDDDD : 0xFFFF5555);
        graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.bonus_chest.note"),
                centerX, 265, 0xFFAAAAAA);
        graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.multi_saplings"),
                centerX, 280, 0xFFFFAA00);
        graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.capacity_note"),
                centerX, 295, 0xFFAAAAAA);
        if (this.statusVisible) {
            graphics.centeredText(this.font, this.status, centerX, 310, this.statusColor);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(this.parent);
    }
}
