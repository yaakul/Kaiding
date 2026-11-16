package me.yaakul.kaiding.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class KaidingScreen extends Screen {

    private static final int COLS = 2;
    private static final int ROWS = 5;
    private static final int W = 150,
            H = 20,
            GAP_X = 24,
            GAP_Y = 24;

    private Button toggleButton;
    private Button modeButton;
    private Button randomButton;
    private Button clickTypeButton;
    private Button hotkeyButton;
    private Button counterDisplay;
    private Button closeButton;
    private Button humanizeButton;
    private CpsSlider minSlider;
    private CpsSlider maxSlider;
    private boolean listeningForKey = false;

    protected KaidingScreen() {
        super(Component.literal("Kaiding"));
    }

    private int colX(int col){
        int totalWidth = COLS * W + (COLS - 1) * GAP_X;
        int left = this.width /2 - totalWidth / 2;
        return left + col * (W + GAP_X);
    }

    private int rowY(int row) {
        int top = this.height / 2 - (ROWS * GAP_Y) / 2;
        return top + row * GAP_Y;
    }

    protected void init(){

        // Clicker toggler
        toggleButton = Button.builder(Component.literal("Autoclicker: " + (ClickerState.enabled ? "ON" : "OFF")), b -> {
            ClickerState.toggle();
            refresh();
        }).bounds(colX(0), rowY(0), W, H).build();

        // Mode toggler (hold/automatic)
        modeButton = Button.builder(Component.literal("Mode: " + (ClickerState.holdMode ? "Hold" : "Toggle")), b -> {
            ClickerState.holdMode = !ClickerState.holdMode;
            refresh();
        }).bounds(colX(0), rowY(1), W, H).build();

        // Type of Click button
        clickTypeButton = Button.builder(Component.literal("Mouse button: " + (ClickerState.button.label)), b -> {
            ClickerState.button = ClickerState.button.next();
            refresh();
        }).bounds(colX(0), rowY(2), W, H).build();

        // Keybinding button
        hotkeyButton = Button.builder(Component.literal("Hotkey: " + KaidingKeybinds.toggleKey.getTranslatedKeyMessage()), b -> {
            listeningForKey = true;
            refresh();
        }).bounds(colX(0), rowY(3), W, H).build();

        // Randomizer toggle
        randomButton = Button.builder(Component.literal("Randomize: " + (ClickerState.randomize ? "ON" : "OFF")), b -> {
            ClickerState.randomize = !ClickerState.randomize;
            ClickerState.minCps = 1.0;
            refresh();
        }).bounds(colX(1), rowY(0), W, H).build();

        // Click distribution button
        humanizeButton = Button.builder(Component.literal("Distribution: " + (ClickerState.humanize ? "Gaussian" : "Uniform")), b -> {
            ClickerState.humanize = !ClickerState.humanize;
            refresh();
        }).bounds(colX(1), rowY(1), W, H).build();

        // Sliders
        minSlider = new CpsSlider(colX(1), rowY(2), W, H, "Min CPS",
                () -> ClickerState.minCps, ClickerState::setMinCps, this::syncSliders);
        maxSlider = new CpsSlider(colX(1), rowY(3), W, H, "Max CPS",
                () -> ClickerState.maxCps, ClickerState::setMaxCps, this::syncSliders);

        // Close menu button
        closeButton = Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(colX(0), rowY(4), W, H)
                .build();

        // Session counter
        counterDisplay = Button.builder(Component.literal("Session clicks: " + String.format("%,d", ClickerState.sessionClicks)), b -> {})
                .bounds(colX(1), rowY(4), W, H).build();
        counterDisplay.active = false;

        this.addRenderableWidget(toggleButton);
        this.addRenderableWidget(modeButton);
        this.addRenderableWidget(clickTypeButton);
        this.addRenderableWidget(hotkeyButton);
        this.addRenderableWidget(randomButton);
        this.addRenderableWidget(humanizeButton);
        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(counterDisplay);
        this.addRenderableWidget(minSlider);
        this.addRenderableWidget(maxSlider);

        refresh();
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent e){
        if(listeningForKey){
            if(e.key() != InputConstants.KEY_ESCAPE){
                bind(InputConstants.getKey(e));
            }
            listeningForKey = false;
            refresh();
            return true;
        }
        return super.keyPressed(e);
    }

    @Override
    public boolean  mouseClicked(@NonNull MouseButtonEvent e, boolean doubleClick){
        if(listeningForKey){
            int button = e.button();
            bind(InputConstants.Type.MOUSE.getOrCreate(button));

            if(button <= InputConstants.MOUSE_BUTTON_RIGHT){
                ClickerState.holdMode = true;
            }

            listeningForKey = false;
            refresh();
            return true;
        }

        return super.mouseClicked(e, doubleClick);
    }

    @Override
    public void removed(){
        ConfigManager.save();
    }

    private void bind(InputConstants.Key k){
        KaidingKeybinds.toggleKey.setKey(k);
        KeyMapping.resetMapping();
        Minecraft.getInstance().options.save();
    }

    private List<KeyMapping> findConflicts(KeyMapping mapping){
        List<KeyMapping> conflicts = new ArrayList<>();
        if(mapping.isUnbound()) return conflicts;

        for(KeyMapping other : Minecraft.getInstance().options.keyMappings){
            if(other != mapping && other.same(mapping)){
                conflicts.add(other);
            }
        }
        return conflicts;
    }

    private void refresh(){
        toggleButton.setMessage(Component.literal(
                "Autoclicker: " + (ClickerState.enabled ? "ON" : "OFF")));
        toggleButton.active = !ClickerState.holdMode;

        modeButton.setMessage(Component.literal(
                "Mode: " + (ClickerState.holdMode ? "Hold" : "Toggle")));

        randomButton.setMessage(Component.literal(
                "Randomize: " + (ClickerState.randomize ? "ON" : "OFF")));
        minSlider.active = ClickerState.randomize;

        humanizeButton.setMessage(Component.literal(
                "Distribution: " + (ClickerState.humanize ? "Gaussian" : "Uniform")));
        humanizeButton.active = ClickerState.randomize; // meaningless without randomization

        clickTypeButton.setMessage(Component.literal("Mouse button: " + ClickerState.button.label));

        if(listeningForKey){
            hotkeyButton.setMessage(Component.literal("> Press a key... <"));
        }
        else{
            MutableComponent label = Component.literal("Hotkey: ").append(KaidingKeybinds.toggleKey.getTranslatedKeyMessage());

            if(!findConflicts(KaidingKeybinds.toggleKey).isEmpty()){
                label = label.withStyle(ChatFormatting.YELLOW);
            }
            hotkeyButton.setMessage(label);

            List<KeyMapping> conflicts = findConflicts(KaidingKeybinds.toggleKey);
                if(conflicts.isEmpty()){
                    hotkeyButton.setTooltip(null);
                }
                else{
                    MutableComponent text = Component.literal("Also Bound to: ");
                    for(int i = 0; i < conflicts.size(); i++){
                        if(i > 0) text.append(", ");
                        text.append(Component.translatable(conflicts.get(i).getName()));
                    }
                    hotkeyButton.setTooltip(Tooltip.create(text));
                }
        }

        counterDisplay.setMessage(Component.literal(
                "Session clicks: " + String.format("%,d", ClickerState.sessionClicks)));

    }

    private void syncSliders(){
        minSlider.sync();
        maxSlider.sync();
    }

    private Component stateLabel(){

        return Component.literal("Clicker: " + (ClickerState.enabled ? "ON" : "OFF"));
    }
}
