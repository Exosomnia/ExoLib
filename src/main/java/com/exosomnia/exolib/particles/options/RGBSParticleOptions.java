package com.exosomnia.exolib.particles.options;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class RGBSParticleOptions implements ParticleOptions {

    public static final Codec<RGBSParticleOptions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(options -> ForgeRegistries.PARTICLE_TYPES.getKey(options.getType())),
                    Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
                    Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
                    Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue),
                    Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale)
            ).apply(instance, (type, red, green, blue, scale) -> new RGBSParticleOptions(ForgeRegistries.PARTICLE_TYPES.getValue(type), red, green, blue, scale)));

//            Codec.FLOAT.listOf().xmap(
//                    floats -> new RGBSParticleOptions(floats.get(0), floats.get(1), floats.get(2), floats.get(3)),
//                    options -> List.of(options.red, options.green, options.blue, options.scale)
//            );

    public final float red;
    public final float green;
    public final float blue;
    public final float scale;
    public final ParticleType<?> type;

    public RGBSParticleOptions(ParticleType<?> type, float red, float green, float blue, float scale) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
        this.type = type;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
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
    public static final Deserializer<RGBSParticleOptions> DUMMY_DESERIALIZER =
            new Deserializer<>() {
                @Override
                public RGBSParticleOptions fromCommand(ParticleType<RGBSParticleOptions> type,
                                                       StringReader reader) {
                    // If you truly don't want command usage,
                    // either throw or return some default fallback
                    throw new UnsupportedOperationException("Commands not supported by this particle.");
                }

                @Override
                public RGBSParticleOptions fromNetwork(ParticleType<RGBSParticleOptions> type,
                                                       FriendlyByteBuf buffer) {
                    // If you don't want to handle the old fromNetwork approach,
                    // either throw or return some default fallback
                    throw new UnsupportedOperationException("Network deserialization is handled by the codec.");
                }
            };
}
