package com.unrealdinnerbone.yastm.proxy;

import com.unrealdinnerbone.yastm.client.gui.GUISelectFrequency2;
import com.unrealdinnerbone.yastm.packet.PacketOpenSetFrequencyGUI;
import net.minecraft.client.Minecraft;

public class ClientProxy extends Proxy {

    @Override
    public void openFrequenyGUI(PacketOpenSetFrequencyGUI message) {
        Minecraft.getMinecraft().displayGuiScreen(new GUISelectFrequency2(message.getBlockPos(), message.getID(), message.getEffect(), message.getParticleEffect()));
    }
}
