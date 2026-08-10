package cl.treecs.skyblockmulti.client;

import cl.treecs.skyblockmulti.SkyblockMultiMod;
import cl.treecs.skyblockmulti.SkyblockMultiMod.BonusChestMode;
import cl.treecs.skyblockmulti.SkyblockMultiMod.TreeOption;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;

public final class SkyblockMultiConfigScreen extends Screen {
    private final Screen parent;
    private final EnumMap<TreeOption, Boolean> treeStates;
    private final EnumMap<TreeOption, Button> treeButtons = new EnumMap<>(TreeOption.class);

    private Button capacityButton;
    private Button radiusButton;
    private Button bonusChestModeButton;
    private Button partyLeaveDifficultyButton;

    private int islandCapacity;
    private int islandRadius;
    private BonusChestMode bonusChestMode;
    private BonusChestMode partyLeaveDifficultyMode;
    private final boolean geometryLocked;
    private final boolean openPacAvailable;

    private Component status = Component.empty();
    private boolean statusVisible;
    private int statusColor = 0xFFAAAAAA;

    public SkyblockMultiConfigScreen(Screen parent) {
        super(Component.translatable("skyblockmulti.config.title"));
        this.parent = parent;
        this.treeStates = SkyblockMultiMod.getConfiguredTreeStates();
        this.islandCapacity = SkyblockMultiMod.getConfiguredCapacity();
        this.islandRadius = SkyblockMultiMod.normalizeRadius(
                SkyblockMultiMod.getConfiguredRadius(),
                this.islandCapacity
        );
        this.bonusChestMode = SkyblockMultiMod.getConfiguredBonusChestMode();
        this.partyLeaveDifficultyMode = SkyblockMultiMod.getConfiguredPartyLeaveDifficultyMode();
        this.geometryLocked = SkyblockMultiMod.isWorldGeometryLocked();
        this.openPacAvailable = SkyblockMultiMod.isOpenPacInstalled();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.capacityButton = this.addRenderableWidget(
                Button.builder(capacityLabel(), button -> cycleCapacity())
                        .bounds(centerX - 155, 52, 150, 20).build()
        );
        this.radiusButton = this.addRenderableWidget(
                Button.builder(radiusLabel(), button -> cycleRadius())
                        .bounds(centerX + 5, 52, 150, 20).build()
        );

        this.capacityButton.active = !geometryLocked;
        this.radiusButton.active = !geometryLocked;

        this.treeButtons.clear();
        TreeOption[] trees = TreeOption.values();
        int startX = centerX - 160;
        int startY = 138;
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
                        .bounds(centerX - 155, 233, 310, 20).build()
        );

        int footerY;
        if (openPacAvailable) {
            this.partyLeaveDifficultyButton = this.addRenderableWidget(
                    Button.builder(partyLeaveDifficultyLabel(), button -> cyclePartyLeaveDifficulty())
                            .bounds(centerX - 155, 258, 310, 20).build()
            );
            footerY = 284;
        } else {
            this.partyLeaveDifficultyButton = null;
            footerY = 258;
        }

        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.reset"), button -> resetDefaults())
                .bounds(centerX - 155, footerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.back"), button -> this.onClose())
                .bounds(centerX - 50, footerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.save"), button -> save())
                .bounds(centerX + 55, footerY, 100, 20).build());
    }

    private void cycleCapacity() {
        if (geometryLocked) return;
        this.islandCapacity = SkyblockMultiMod.getNextConfiguredCapacity(this.islandCapacity);
        this.islandRadius = SkyblockMultiMod.normalizeRadius(this.islandRadius, this.islandCapacity);
        refreshGeometryLabels();
        clearStatus();
    }

    private void cycleRadius() {
        if (geometryLocked) return;
        this.islandRadius = SkyblockMultiMod.getNextConfiguredRadius(this.islandRadius, this.islandCapacity);
        refreshGeometryLabels();
        clearStatus();
    }

    private void refreshGeometryLabels() {
        if (this.capacityButton != null) this.capacityButton.setMessage(capacityLabel());
        if (this.radiusButton != null) this.radiusButton.setMessage(radiusLabel());
    }

    private Component capacityLabel() {
        return Component.translatable("skyblockmulti.config.capacity.button", this.islandCapacity);
    }

    private Component radiusLabel() {
        int chunks = SkyblockMultiMod.getRadiusChunks(this.islandRadius, this.islandCapacity);
        return Component.translatable(
                this.islandCapacity == 24
                        ? "skyblockmulti.config.radius.outer.button"
                        : "skyblockmulti.config.radius.button",
                chunks
        );
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

    private void cyclePartyLeaveDifficulty() {
        if (!openPacAvailable) return;
        this.partyLeaveDifficultyMode = this.partyLeaveDifficultyMode.next();
        if (this.partyLeaveDifficultyButton != null) {
            this.partyLeaveDifficultyButton.setMessage(partyLeaveDifficultyLabel());
        }
        clearStatus();
    }

    private Component partyLeaveDifficultyLabel() {
        return Component.translatable(
                "skyblockmulti.config.party_leave.mode.button",
                Component.translatable(
                        "skyblockmulti.config.bonus_chest.mode." + partyLeaveDifficultyMode.configKey()
                )
        );
    }

    private void resetDefaults() {
        if (!geometryLocked) {
            this.islandCapacity = SkyblockMultiMod.DEFAULT_CAPACITY;
            this.islandRadius = SkyblockMultiMod.DEFAULT_RADIUS;
            refreshGeometryLabels();
        }

        for (TreeOption tree : TreeOption.values()) {
            treeStates.put(tree, true);
            Button button = treeButtons.get(tree);
            if (button != null) button.setMessage(treeLabel(tree));
        }

        this.bonusChestMode = BonusChestMode.BEGINNER;
        this.partyLeaveDifficultyMode = BonusChestMode.BEGINNER;
        if (this.bonusChestModeButton != null) this.bonusChestModeButton.setMessage(bonusChestModeLabel());
        if (this.partyLeaveDifficultyButton != null) {
            this.partyLeaveDifficultyButton.setMessage(partyLeaveDifficultyLabel());
        }
        setStatus("skyblockmulti.config.status.defaults", 0xFFFFFF55);
    }

    private int enabledTreeCount() {
        int count = 0;
        for (TreeOption tree : TreeOption.values()) {
            if (Boolean.TRUE.equals(treeStates.get(tree))) count++;
        }
        return count;
    }

    private void save() {
        if (enabledTreeCount() == 0) {
            setStatus("skyblockmulti.config.status.one_tree", 0xFFFF5555);
            return;
        }

        int normalizedCapacity = SkyblockMultiMod.normalizeCapacity(this.islandCapacity);
        int normalizedRadius = SkyblockMultiMod.normalizeRadius(this.islandRadius, normalizedCapacity);

        if (SkyblockMultiMod.saveConfiguration(
                normalizedRadius,
                normalizedCapacity,
                treeStates,
                bonusChestMode,
                partyLeaveDifficultyMode
        )) {
            this.islandCapacity = normalizedCapacity;
            this.islandRadius = normalizedRadius;
            refreshGeometryLabels();
            setStatus("skyblockmulti.config.status.saved", 0xFF55FF55);
        } else {
            setStatus("skyblockmulti.config.status.save_failed", 0xFFFF5555);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int centerX = this.width / 2;

        graphics.centeredText(this.font, this.title, centerX, 18, 0xFFFFFFFF);
        graphics.centeredText(
                this.font,
                Component.translatable("skyblockmulti.config.capacity.label"),
                centerX - 80,
                37,
                0xFFDDDDDD
        );
        graphics.centeredText(
                this.font,
                Component.translatable("skyblockmulti.config.radius.label"),
                centerX + 80,
                37,
                0xFFDDDDDD
        );

        if (this.islandCapacity == 24) {
            int outerChunks = SkyblockMultiMod.getRadiusChunks(this.islandRadius, this.islandCapacity);
            int innerRadius = SkyblockMultiMod.getInnerRadius(this.islandRadius, this.islandCapacity);
            int innerChunks = innerRadius / 16;
            graphics.centeredText(
                    this.font,
                    Component.translatable(
                            "skyblockmulti.config.layout_summary.dual",
                            this.islandRadius,
                            outerChunks,
                            innerRadius,
                            innerChunks
                    ),
                    centerX,
                    80,
                    0xFF55FFFF
            );
            graphics.centeredText(
                    this.font,
                    Component.translatable("skyblockmulti.config.layout_summary.dual_policy"),
                    centerX,
                    93,
                    0xFF55FFFF
            );
        } else {
            graphics.centeredText(
                    this.font,
                    Component.translatable(
                            "skyblockmulti.config.layout_summary.single",
                            this.islandCapacity,
                            this.islandRadius,
                            SkyblockMultiMod.getRadiusChunks(this.islandRadius, this.islandCapacity),
                            SkyblockMultiMod.getApproxNeighborDistance(this.islandRadius, this.islandCapacity)
                    ),
                    centerX,
                    80,
                    0xFF55FFFF
            );
            graphics.centeredText(
                    this.font,
                    Component.translatable("skyblockmulti.config.layout_summary.random"),
                    centerX,
                    93,
                    0xFF55FFFF
            );
        }

        if (openPacAvailable) {
            graphics.centeredText(
                    this.font,
                    Component.translatable("skyblockmulti.config.openpac_capacity_note"),
                    centerX,
                    106,
                    0xFFAAAAAA
            );
        }

        graphics.centeredText(
                this.font,
                Component.translatable(
                        geometryLocked
                                ? "skyblockmulti.config.geometry.locked"
                                : "skyblockmulti.config.geometry.unlocked"
                ),
                centerX,
                119,
                geometryLocked ? 0xFFFFAA00 : 0xFF55FF55
        );

        graphics.centeredText(this.font,
                Component.translatable("skyblockmulti.config.allowed_trees", enabledTreeCount(), TreeOption.values().length),
                centerX, 129, enabledTreeCount() > 0 ? 0xFFDDDDDD : 0xFFFF5555);

        int bonusNoteY = openPacAvailable ? 309 : 283;
        int multiSaplingsY = openPacAvailable ? 339 : 298;
        int statusY = openPacAvailable ? 354 : 313;

        graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.bonus_chest.note"),
                centerX, bonusNoteY, 0xFFAAAAAA);

        if (openPacAvailable) {
            graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.party_leave.mode.note"),
                    centerX, 324, 0xFF55FFFF);
        }

        graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.multi_saplings"),
                centerX, multiSaplingsY, 0xFFFFAA00);

        if (this.statusVisible) {
            graphics.centeredText(this.font, this.status, centerX, statusY, this.statusColor);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(this.parent);
    }
}
