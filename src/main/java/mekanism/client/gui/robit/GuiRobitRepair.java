package mekanism.client.gui.robit;

import mekanism.client.gui.element.text.BackgroundType;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.entity.robit.RepairRobitContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuiRobitRepair extends GuiRobit<RepairRobitContainer> implements ContainerListener {

    //Use the vanilla anvil's gui texture
    private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
    private final Player player;
    private GuiTextField itemNameField;

    public GuiRobitRepair(RepairRobitContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.player = inv.player;
        inventoryLabelY += 1;
        titleLabelX = 60;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        //TODO - 1.20: Validate this behaves properly and there is no new version of sendRepeatsToGui
        itemNameField = addRenderableWidget(new GuiTextField(this, 60, 21, 103, 12));
        itemNameField.setCanLoseFocus(false);
        itemNameField.setTextColor(-1);
        itemNameField.setTextColorUneditable(-1);
        itemNameField.setBackground(BackgroundType.NONE);
        itemNameField.setMaxLength(50);
        itemNameField.setResponder(this::onNameChanged);
        setInitialFocus(itemNameField);
        itemNameField.setEditable(false);
        menu.removeSlotListener(this);
        menu.addSlotListener(this);
    }

    private void onNameChanged(String newText) {
        if (!newText.isEmpty()) {
            Slot slot = menu.getSlot(0);
            if (slot.hasItem() && !slot.getItem().hasCustomHoverName() && newText.equals(slot.getItem().getHoverName().getString())) {
                newText = "";
            }
            menu.setItemName(newText);
            getMinecraft().player.connection.send(new ServerboundRenameItemPacket(newText));
        }
    }

    @Override
    public void removed() {
        super.removed();
        menu.removeSlotListener(this);
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawString(guiGraphics, title, titleLabelX, titleLabelY, titleTextColor());
        int maximumCost = menu.getCost();
        if (maximumCost > 0) {
            int k = 0x80FF20;
            boolean flag = true;
            Component component = MekanismLang.REPAIR_COST.translate(maximumCost);
            if (maximumCost >= 40 && !getMinecraft().player.isCreative()) {
                component = MekanismLang.REPAIR_EXPENSIVE.translate();
                k = 0xFF6060;
            } else {
                Slot slot = menu.getSlot(2);
                if (!slot.hasItem()) {
                    flag = false;
                } else if (!slot.mayPickup(player)) {
                    k = 0xFF6060;
                }
            }

            if (flag) {
                int width = imageWidth - 8 - getStringWidth(component) - 2;
                guiGraphics.fill(width - 2, 67, imageWidth - 8, 79, 0x4F000000);
                guiGraphics.drawString(getFont(), component, width, 69, k);
                MekanismRenderer.resetColor();
            }
        }
        drawString(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected boolean shouldOpenGui(RobitGuiType guiType) {
        return guiType != RobitGuiType.REPAIR;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        MekanismRenderer.resetColor();
        guiGraphics.blit(ANVIL_RESOURCE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        guiGraphics.blit(ANVIL_RESOURCE, leftPos + 59, topPos + 20, 0, imageHeight + (menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
        if ((menu.getSlot(0).hasItem() || menu.getSlot(1).hasItem()) && !menu.getSlot(2).hasItem()) {
            guiGraphics.blit(ANVIL_RESOURCE, leftPos + 99, topPos + 45, imageWidth, 0, 28, 21);
        }
    }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu container, int slotID, @NotNull ItemStack stack) {
        if (slotID == 0) {
            itemNameField.setText(stack.isEmpty() ? "" : stack.getHoverName().getString());
            itemNameField.setEditable(!stack.isEmpty());
            setFocused(itemNameField);
        }
    }

    @Override
    public void dataChanged(@NotNull AbstractContainerMenu container, int slotID, int value) {
    }
}