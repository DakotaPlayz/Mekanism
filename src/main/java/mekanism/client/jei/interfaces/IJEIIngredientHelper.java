package mekanism.client.jei.interfaces;

import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;

public interface IJEIIngredientHelper {

    /**
     * Gets the ingredient under the mouse.
     *
     * @param mouseX X position of mouse.
     * @param mouseY Y position of mouse.
     *
     * @return Ingredient or {@code null}.
     *
     * @apiNote isMouseOver is called before this method, the positions are mainly provided for use by things like
     * {@link mekanism.client.gui.element.scroll.GuiSlotScroll} that may have different ingredients based on where in the element the mouse is.
     */
    @Nullable
    Object getIngredient(double mouseX, double mouseY);

    /**
     * Gets the bounds of the ingredient for where it can be clicked.
     */
    Rect2i getIngredientBounds(double mouseX, double mouseY);
}