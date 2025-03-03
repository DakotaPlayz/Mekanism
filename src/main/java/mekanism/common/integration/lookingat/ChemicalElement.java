package mekanism.common.integration.lookingat;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.math.MathUtils;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ChemicalElement extends LookingAtElement {

    @NotNull
    protected final ChemicalStack<?> stored;
    protected final long capacity;

    public ChemicalElement(@NotNull ChemicalStack<?> stored, long capacity) {
        super(0xFF000000, 0xFFFFFF);
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public int getScaledLevel(int level) {
        if (capacity == 0 || stored.getAmount() == Long.MAX_VALUE) {
            return level;
        }
        return MathUtils.clampToInt(level * (double) stored.getAmount() / capacity);
    }

    public ChemicalType getChemicalType() {
        return ChemicalType.getTypeFor(stored);
    }

    @NotNull
    public ChemicalStack<?> getStored() {
        return stored;
    }

    public long getCapacity() {
        return capacity;
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return stored.isEmpty() ? null : MekanismRenderer.getChemicalTexture(stored.getType());
    }

    @Override
    public Component getText() {
        long amount = stored.getAmount();
        if (amount == Long.MAX_VALUE) {
            return MekanismLang.GENERIC_STORED.translate(stored.getType(), MekanismLang.INFINITE);
        }
        return MekanismLang.GENERIC_STORED_MB.translate(stored.getType(), TextUtils.format(amount));
    }

    @Override
    protected boolean applyRenderColor() {
        MekanismRenderer.color(stored.getType());
        return true;
    }
}