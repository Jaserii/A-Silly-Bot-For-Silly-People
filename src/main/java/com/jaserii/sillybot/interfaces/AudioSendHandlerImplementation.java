package com.jaserii.sillybot.interfaces;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioSendHandlerImplementation implements AudioSendHandler {

    @Override
    public boolean canProvide() {
        return false;
    }

    @Override
    public @Nullable ByteBuffer provide20MsAudio() {
        return null;
    }
}
