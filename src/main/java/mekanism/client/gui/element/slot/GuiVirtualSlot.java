package mekanism.client.gui.element.slot;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.VirtualSlotContainerScreen;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.jei.interfaces.IJEIIngredientHelper;
import mekanism.common.inventory.container.IGUIWindow;
import mekanism.common.inventory.container.slot.IVirtualSlot;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.slot.VirtualInventoryContainerSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiVirtualSlot extends GuiSlot implements IJEIIngredientHelper {

    private IVirtualSlot virtualSlot;

    public GuiVirtualSlot(@Nullable IGUIWindow window, SlotType type, IGuiWrapper gui, int x, int y, VirtualInventoryContainerSlot containerSlot) {
        this(type, gui, x, y);
        if (containerSlot != null) {
            SlotOverlay slotOverlay = containerSlot.getSlotOverlay();
            if (slotOverlay != null) {
                with(slotOverlay);
            }
            updateVirtualSlot(window, containerSlot);
        }
    }

    public GuiVirtualSlot(SlotType type, IGuiWrapper gui, int x, int y) {
        super(type, gui, x, y);
        //Virtual slots need to render the hovered overlay as they don't let vanilla render it
        setRenderHover(true);
    }

    public boolean isElementForSlot(IVirtualSlot virtualSlot) {
        return this.virtualSlot == virtualSlot;
    }

    public void updateVirtualSlot(@Nullable IGUIWindow window, @NotNull IVirtualSlot virtualSlot) {
        this.virtualSlot = virtualSlot;
        this.virtualSlot.updatePosition(window, () -> relativeX + 1, () -> relativeY + 1);
    }

    @Override
    protected void drawContents(@NotNull GuiGraphics guiGraphics) {
        if (virtualSlot != null) {
            ItemStack stack = virtualSlot.getStackToRender();
            if (!stack.isEmpty()) {
                int xPos = relativeX + 1;
                int yPos = relativeY + 1;
                if (virtualSlot.shouldDrawOverlay()) {
                    guiGraphics.fill(xPos, yPos, xPos + 16, yPos + 16, DEFAULT_HOVER_COLOR);
                }
                gui().renderItemWithOverlay(guiGraphics, stack, xPos, yPos, 1, virtualSlot.getTooltipOverride());
            }
        }
    }

    @Nullable
    @Override
    public GuiElement mouseClickedNested(double mouseX, double mouseY, int button) {
        if (mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height) {
            IGuiWrapper gui = gui();
            if (gui instanceof VirtualSlotContainerScreen<?> screen && virtualSlot != null) {
                //Redirect to a copy of vanilla logic
                return screen.slotClicked(virtualSlot.getSlot(), button) ? this : null;
            }
        }
        return super.mouseClickedNested(mouseX, mouseY, button);
    }

    @Nullable
    @Override
    public Object getIngredient(double mouseX, double mouseY) {
        //Note: We can get away with just using the stack to render
        return virtualSlot == null ? null : virtualSlot.getStackToRender();
    }

    @Override
    public Rect2i getIngredientBounds(double mouseX, double mouseY) {
        return new Rect2i(getX() + 1, getY() + 1, 16, 16);
    }
}