package com.bearfininteractive.bearfinutilities.gui;

import com.bearfininteractive.bearfinutilities.items.ItemPDA;
import com.bearfininteractive.bearfinutilities.network.ModNetwork;
import com.bearfininteractive.bearfinutilities.network.PacketSavePDAAccount;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiPDA extends GuiScreen {

    private static final ResourceLocation DIRT_BG =
        new ResourceLocation("textures/gui/options_background.png");

    private enum State { NO_ACCOUNT, SETUP }

    private static final String[] COLOR_CODES = {"§f", "§e", "§6", "§c", "§a", "§b", "§9", "§d"};
    private static final int[] COLOR_RGB = {
        0xFFFFFF, 0xFFFF55, 0xFFAA00, 0xFF5555,
        0x55FF55, 0x55FFFF, 0x5555FF, 0xFF55FF
    };

    private static final int BTN_ADD_ACCOUNT = 0;
    private static final int BTN_SAVE = 1;

    private final ItemStack pdaStack;
    private final EnumHand hand;
    private State state;
    private GuiTextField usernameField;
    private String selectedColor;

    private int colorStartX;
    private int colorY;

    public GuiPDA(ItemStack stack, EnumHand hand) {
        this.pdaStack = stack;
        this.hand = hand;
        this.state = ItemPDA.isSetUp(stack) ? State.SETUP : State.NO_ACCOUNT;
        this.selectedColor = ItemPDA.getColor(stack);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        int cx = width / 2;
        int cy = height / 2;

        if (state == State.NO_ACCOUNT) {
            buttonList.add(new GuiButton(BTN_ADD_ACCOUNT, cx - 60, cy + 10, 120, 20, "Add Account"));
        } else {
            usernameField = new GuiTextField(0, fontRenderer, cx - 75, cy - 30, 150, 20);
            usernameField.setMaxStringLength(24);
            usernameField.setText(ItemPDA.getUsername(pdaStack));
            usernameField.setFocused(true);

            colorStartX = cx - (COLOR_CODES.length * 18) / 2;
            colorY = cy + 10;

            buttonList.add(new GuiButton(BTN_SAVE, cx - 50, cy + 45, 100, 20, "Save"));
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        if (usernameField != null) usernameField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int cx = width / 2;
        int cy = height / 2;

        // Panel with dirt texture background
        drawDirtPanel(cx - 110, cy - 75, cx + 110, cy + 75);
        drawCenteredString(fontRenderer, "§aPDA", cx, cy - 67, 0xFFFFFF);
        drawHorizontalLine(cx - 105, cx + 105, cy - 55, 0xFF555555);

        if (state == State.NO_ACCOUNT) {
            drawCenteredString(fontRenderer, "§cNo account!", cx, cy - 10, 0xFFFFFF);
        } else {
            drawString(fontRenderer, "Username:", cx - 75, cy - 45, 0xAAAAAA);
            drawString(fontRenderer, "Color:", cx - 75, colorY - 12, 0xAAAAAA);

            for (int i = 0; i < COLOR_CODES.length; i++) {
                int sx = colorStartX + i * 18;
                if (COLOR_CODES[i].equals(selectedColor)) {
                    drawRect(sx - 2, colorY - 2, sx + 18, colorY + 18, 0xFFFFFFFF);
                }
                drawRect(sx, colorY, sx + 16, colorY + 16, 0xFF000000 | COLOR_RGB[i]);
            }

            if (usernameField != null) usernameField.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawDirtPanel(int x1, int y1, int x2, int y2) {
        mc.getTextureManager().bindTexture(DIRT_BG);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        float s = 32.0f;
        buf.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(x2, y1, 0).tex(x2 / s, y1 / s).color(64, 64, 64, 255).endVertex();
        buf.pos(x1, y1, 0).tex(x1 / s, y1 / s).color(64, 64, 64, 255).endVertex();
        buf.pos(x1, y2, 0).tex(x1 / s, y2 / s).color(64, 64, 64, 255).endVertex();
        buf.pos(x2, y2, 0).tex(x2 / s, y2 / s).color(64, 64, 64, 255).endVertex();
        tess.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == BTN_ADD_ACCOUNT) {
            state = State.SETUP;
            selectedColor = "§f";
            initGui();
        } else if (button.id == BTN_SAVE) {
            String username = usernameField != null ? usernameField.getText().trim() : "";
            if (!username.isEmpty()) {
                ModNetwork.CHANNEL.sendToServer(new PacketSavePDAAccount(hand, username, selectedColor));
                mc.player.closeScreen();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (usernameField != null) usernameField.mouseClicked(mouseX, mouseY, mouseButton);

        if (state == State.SETUP) {
            for (int i = 0; i < COLOR_CODES.length; i++) {
                int sx = colorStartX + i * 18;
                if (mouseX >= sx && mouseX < sx + 16 && mouseY >= colorY && mouseY < colorY + 16) {
                    selectedColor = COLOR_CODES[i];
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.player.closeScreen();
            return;
        }
        if (usernameField != null) usernameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
