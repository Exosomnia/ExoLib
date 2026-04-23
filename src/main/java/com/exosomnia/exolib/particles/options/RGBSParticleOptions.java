package com.exosomnia.exolib.particles.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class RGBSParticleOptions implements ParticleOptions {

    public static final MapCodec<RGBSParticleOptions> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(options -> BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType())),
                    Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
                    Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
                    Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue),
                    Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale)
            ).apply(instance, (type, red, green, blue, scale) -> new RGBSParticleOptions(BuiltInRegistries.PARTICLE_TYPE.get(type), red, green, blue, scale)));

    public static final StreamCodec<RegistryFriendlyByteBuf, RGBSParticleOptions> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.PARTICLE_TYPE), RGBSParticleOptions::getType,
                    ByteBufCodecs.FLOAT, options -> options.red,
                    ByteBufCodecs.FLOAT, options -> options.green,
                    ByteBufCodecs.FLOAT, options -> options.blue,
                    ByteBufCodecs.FLOAT, options -> options.scale,
                    RGBSParticleOptions::new
            );

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
}
