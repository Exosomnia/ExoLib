package com.exosomnia.exolib.particles;

import com.exosomnia.exolib.ExoLib;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class GridParticleOptions implements ParticleOptions {

    public static final Codec<GridParticleOptions> CODEC =
            Codec.FLOAT.listOf().xmap(
                    floats -> new GridParticleOptions(floats.get(0), floats.get(1), floats.get(2), floats.get(3)),
                    options -> List.of(options.red, options.green, options.blue, options.scale)
            );

    public final float red;
    public final float green;
    public final float blue;
    public final float scale;

    public GridParticleOptions(float red, float green, float blue, float scale) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
    }

    @Override
    public ParticleType<?> getType() {
        return ExoLib.GRID_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(red);
        friendlyByteBuf.writeFloat(green);
        friendlyByteBuf.writeFloat(blue);
        friendlyByteBuf.writeFloat(scale);
    }

    @Override
    public String writeToString() {
        return String.format("%f %f %f %f", red, green, blue, scale);
    }

    /** Provide a dummy deserializer that does nothing (or throws). Thank you GPT */
    public static final ParticleOptions.Deserializer<GridParticleOptions> DUMMY_DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public GridParticleOptions fromCommand(ParticleType<GridParticleOptions> type,
                                                       StringReader reader) {
                    // If you truly don't want command usage,
                    // either throw or return some default fallback
                    throw new UnsupportedOperationException("Commands not supported by this particle.");
                }

                @Override
                public GridParticleOptions fromNetwork(ParticleType<GridParticleOptions> type,
                                                       FriendlyByteBuf buffer) {
                    // If you don't want to handle the old fromNetwork approach,
                    // either throw or return some default fallback
                    throw new UnsupportedOperationException("Network deserialization is handled by the codec.");
                }
            };
}
