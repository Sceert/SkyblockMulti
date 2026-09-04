package cl.treecs.skyblockmulti.client;

import cl.treecs.skyblockmulti.NexusFoundation;
import cl.treecs.skyblockmulti.SkyblockMultiMod;
import cl.treecs.skyblockmulti.SkyblockMultiMod.BonusChestMode;
import cl.treecs.skyblockmulti.tree.TreeCatalog;
import cl.treecs.skyblockmulti.tree.TreeDefinition;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SkyblockMultiConfigScreen extends Screen {
    private enum Page { WORLD, TREES, PROGRESSION }

    private final Screen parent;
    private final Map<String, Boolean> treeStates;
    private final Map<String, Button> treeButtons = new LinkedHashMap<>();

    private Button capacityButton;
    private Button radiusButton;
    private Button bonusChestModeButton;
    private Button partyLeaveDifficultyButton;
    private Button endPortalEyesButton;

    private int islandCapacity;
    private int islandRadius;
    private BonusChestMode bonusChestMode;
    private BonusChestMode partyLeaveDifficultyMode;
    private int endPortalEyesPercent;
    private final boolean geometryLocked;
    private final boolean endPortalConfigurationLocked;
    private final boolean openPacAvailable;

    private Component status = Component.empty();
    private boolean statusVisible;
    private int statusColor = 0xFFAAAAAA;
    private Page page = Page.WORLD;

    public SkyblockMultiConfigScreen(Screen parent) {
        super(Component.translatable("skyblockmulti.config.title"));
        this.parent = parent;
        this.treeStates = SkyblockMultiMod.getConfiguredTreeStatesById();
        this.islandCapacity = SkyblockMultiMod.getConfiguredCapacity();
        this.islandRadius = SkyblockMultiMod.normalizeRadius(
                SkyblockMultiMod.getConfiguredRadius(),
                this.islandCapacity
        );
        this.bonusChestMode = SkyblockMultiMod.getConfiguredBonusChestMode();
        this.partyLeaveDifficultyMode = SkyblockMultiMod.getConfiguredPartyLeaveDifficultyMode();
        this.endPortalEyesPercent = NexusFoundation.getConfiguredEndPortalEyesPercent();
        this.geometryLocked = SkyblockMultiMod.isWorldGeometryLocked();
        this.endPortalConfigurationLocked = NexusFoundation.isEndPortalConfigurationLocked();
        this.openPacAvailable = SkyblockMultiMod.isOpenPacInstalled();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int contentTop = 58;
        int footerY = Math.max(contentTop + 100, this.height - 28);

        this.capacityButton = null;
        this.radiusButton = null;
        this.bonusChestModeButton = null;
        this.partyLeaveDifficultyButton = null;
        this.endPortalEyesButton = null;
        this.treeButtons.clear();

        addPageButton(Page.WORLD, centerX - 159, "skyblockmulti.config.tab.world");
        addPageButton(Page.TREES, centerX - 53, "skyblockmulti.config.tab.trees");
        addPageButton(Page.PROGRESSION, centerX + 53, "skyblockmulti.config.tab.progression");

        if (page == Page.WORLD) {
            this.capacityButton = this.addRenderableWidget(
                    Button.builder(capacityLabel(), button -> cycleCapacity())
                            .bounds(centerX - 155, contentTop + 17, 150, 20).build());
            this.radiusButton = this.addRenderableWidget(
                    Button.builder(radiusLabel(), button -> cycleRadius())
                            .bounds(centerX + 5, contentTop + 17, 150, 20).build());
            this.capacityButton.active = !geometryLocked;
            this.radiusButton.active = !geometryLocked;
            this.bonusChestModeButton = this.addRenderableWidget(
                    Button.builder(bonusChestModeLabel(), button -> cycleBonusChestMode())
                            .bounds(centerX - 155, contentTop + 105, 310, 20).build());
            if (openPacAvailable) {
                this.partyLeaveDifficultyButton = this.addRenderableWidget(
                        Button.builder(partyLeaveDifficultyLabel(), button -> cyclePartyLeaveDifficulty())
                                .bounds(centerX - 155, contentTop + 130, 310, 20).build());
            }
        } else if (page == Page.TREES) {
            var trees = TreeCatalog.builtIns();
            int startX = centerX - 160;
            for (int i = 0; i < trees.size(); i++) {
                TreeDefinition tree = trees.get(i);
                int x = startX + (i % 3) * 108;
                int y = contentTop + 22 + (i / 3) * 25;
                Button button = Button.builder(treeLabel(tree), clicked -> toggleTree(tree, clicked))
                        .bounds(x, y, 104, 20).build();
                this.treeButtons.put(tree.id(), button);
                this.addRenderableWidget(button);
            }
        } else {
            this.endPortalEyesButton = this.addRenderableWidget(
                    Button.builder(endPortalEyesLabel(), button -> cycleEndPortalEyes())
                            .bounds(centerX - 155, contentTop + 25, 310, 20).build());
            this.endPortalEyesButton.active = !endPortalConfigurationLocked;
        }

        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.reset"), button -> resetDefaults())
                .bounds(centerX - 155, footerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.back"), button -> this.onClose())
                .bounds(centerX - 50, footerY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("skyblockmulti.config.save"), button -> save())
                .bounds(centerX + 55, footerY, 100, 20).build());
    }

    private void addPageButton(Page target, int x, String translationKey) {
        Button button = this.addRenderableWidget(
                Button.builder(Component.translatable(translationKey), ignored -> {
                    page = target;
                    rebuildWidgets();
                }).bounds(x, 31, 104, 20).build()
        );
        button.active = page != target;
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

    private void toggleTree(TreeDefinition tree, Button button) {
        treeStates.put(tree.id(), !Boolean.TRUE.equals(treeStates.get(tree.id())));
        button.setMessage(treeLabel(tree));
        clearStatus();
    }

    private Component treeLabel(TreeDefinition tree) {
        boolean enabled = Boolean.TRUE.equals(treeStates.get(tree.id()));
        return Component.translatable(
                "skyblockmulti.config.tree.button",
                Component.translatable(tree.translationKey()),
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

    private void cycleEndPortalEyes() {
        if (endPortalConfigurationLocked) return;
        this.endPortalEyesPercent = NexusFoundation.getNextEndPortalEyesPercent(
                this.endPortalEyesPercent
        );
        this.endPortalEyesButton.setMessage(endPortalEyesLabel());
        clearStatus();
    }

    private Component endPortalEyesLabel() {
        return Component.translatable(
                endPortalConfigurationLocked
                        ? "skyblockmulti.config.end_portal.eyes.locked_button"
                        : "skyblockmulti.config.end_portal.eyes.button",
                this.endPortalEyesPercent
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

        for (TreeDefinition tree : TreeCatalog.builtIns()) {
            treeStates.put(tree.id(), true);
            Button button = treeButtons.get(tree.id());
            if (button != null) button.setMessage(treeLabel(tree));
        }

        this.bonusChestMode = BonusChestMode.BEGINNER;
        this.partyLeaveDifficultyMode = BonusChestMode.BEGINNER;
        if (!endPortalConfigurationLocked) {
            this.endPortalEyesPercent = NexusFoundation.DEFAULT_END_EYES;
        }
        if (this.bonusChestModeButton != null) this.bonusChestModeButton.setMessage(bonusChestModeLabel());
        if (this.partyLeaveDifficultyButton != null) {
            this.partyLeaveDifficultyButton.setMessage(partyLeaveDifficultyLabel());
        }
        if (this.endPortalEyesButton != null) {
            this.endPortalEyesButton.setMessage(endPortalEyesLabel());
        }
        setStatus("skyblockmulti.config.status.defaults", 0xFFFFFF55);
    }

    private int enabledTreeCount() {
        int count = 0;
        for (TreeDefinition tree : TreeCatalog.builtIns()) {
            if (Boolean.TRUE.equals(treeStates.get(tree.id()))) count++;
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

        boolean generalSaved = SkyblockMultiMod.saveConfigurationByTreeId(
                normalizedRadius,
                normalizedCapacity,
                treeStates,
                bonusChestMode,
                partyLeaveDifficultyMode
        );
        boolean portalSaved = endPortalConfigurationLocked
                || NexusFoundation.saveConfiguredEndPortalEyesPercent(endPortalEyesPercent);

        if (generalSaved && portalSaved) {
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
        if (page == Page.WORLD) {
            graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.capacity.label"), centerX - 80, 61, 0xFFDDDDDD);
            graphics.centeredText(this.font, Component.translatable("skyblockmulti.config.radius.label"), centerX + 80, 61, 0xFFDDDDDD);
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
                    100,
                    0xFF55FFFF
            );
            graphics.centeredText(
                    this.font,
                    Component.translatable("skyblockmulti.config.layout_summary.dual_policy"),
                    centerX,
                    113,
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
                    100,
                    0xFF55FFFF
            );
            graphics.centeredText(
                    this.font,
                    Component.translatable("skyblockmulti.config.layout_summary.random"),
                    centerX,
                    113,
                    0xFF55FFFF
            );
        }

        if (openPacAvailable) {
            graphics.centeredText(
                    this.font,
                    Component.translatable("skyblockmulti.config.openpac_capacity_note"),
                    centerX,
                    126,
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
                139,
                geometryLocked ? 0xFFFFAA00 : 0xFF55FF55
        );
        } else if (page == Page.TREES) {
            graphics.centeredText(this.font,
                Component.translatable(
                        "skyblockmulti.config.allowed_trees",
                        enabledTreeCount(),
                        TreeCatalog.builtIns().size()
                ),
                centerX, 61, enabledTreeCount() > 0 ? 0xFFDDDDDD : 0xFFFF5555);
        } else {
            graphics.centeredText(this.font,
                    Component.translatable("skyblockmulti.config.end_portal.title"),
                    centerX, 65, 0xFFDDDDDD);
        }

        if (this.statusVisible) {
            graphics.centeredText(
                    this.font,
                    this.status,
                    centerX,
                    Math.max(170, this.height - 42),
                    this.statusColor
            );
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(this.parent);
    }
}
