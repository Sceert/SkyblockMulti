package cl.treecs.skyblockmulti.client;

import cl.treecs.skyblockmulti.network.DifficultySelectPayload;
import cl.treecs.skyblockmulti.network.BackToTreeSelectionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class DifficultySelectionScreen extends Screen {
    private static final String[] NAMES = {"extreme", "hard", "standard", "easy"};

    public DifficultySelectionScreen() {
        super(Component.translatable("skyblockmulti.difficulty.title"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int startY = Math.max(70, height / 2 - 70);
        for (int index = 0; index < NAMES.length; index++) {
            int value = index + 1;
            String name = NAMES[index];
            Button button = Button.builder(
                            Component.translatable("skyblockmulti.difficulty." + name),
                            clicked -> {
                                if (value == 1) {
                                    confirmExtreme();
                                } else {
                                    select(value);
                                }
                            }
                    )
                    .bounds(centerX - 150, startY + index * 34, 300, 26)
                    .tooltip(Tooltip.create(Component.translatable(
                            "skyblockmulti.difficulty." + name + ".hover"
                    )))
                    .build();
            button.active = ClientPlayNetworking.canSend(DifficultySelectPayload.TYPE);
            addRenderableWidget(button);
        }

        Button changeTreeButton = Button.builder(
                        Component.translatable("skyblockmulti.difficulty.change_tree"),
                        clicked -> backToTreeSelection()
                )
                .bounds(centerX - 90, height - 28, 180, 20)
                .build();
        changeTreeButton.active = ClientPlayNetworking.canSend(BackToTreeSelectionPayload.TYPE);
        addRenderableWidget(changeTreeButton);
    }

    private void backToTreeSelection() {
        if (!ClientPlayNetworking.canSend(BackToTreeSelectionPayload.TYPE)) return;
        ClientPlayNetworking.send(BackToTreeSelectionPayload.INSTANCE);
        onClose();
    }

    private void confirmExtreme() {
        this.minecraft.setScreenAndShow(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        select(1);
                    } else {
                        this.minecraft.setScreenAndShow(this);
                    }
                },
                Component.translatable("skyblockmulti.difficulty.extreme.warning.title"),
                Component.translatable("skyblockmulti.difficulty.extreme.warning.body"),
                Component.translatable("skyblockmulti.difficulty.extreme.warning.confirm"),
                Component.translatable("skyblockmulti.difficulty.extreme.warning.cancel")
        ));
    }

    private void select(int value) {
        if (!ClientPlayNetworking.canSend(DifficultySelectPayload.TYPE)) return;
        ClientPlayNetworking.send(new DifficultySelectPayload(value));
        onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(font, title, width / 2, 24, 0xFFFFAA00);
        graphics.centeredText(
                font,
                Component.translatable("skyblockmulti.difficulty.instruction"),
                width / 2,
                44,
                0xFFFFFFFF
        );
        graphics.centeredText(
                font,
                Component.translatable("skyblockmulti.difficulty.note"),
                width / 2,
                Math.min(height - 42, Math.max(70, height / 2 - 70) + 145),
                0xFFAAAAAA
        );
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(null);
    }
}
