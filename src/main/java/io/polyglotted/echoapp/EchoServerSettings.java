package io.polyglotted.echoapp;

import io.polyglotted.applauncher.settings.Attribute;
import io.polyglotted.applauncher.settings.Settings;

@Settings
public interface EchoServerSettings {

    @Attribute(name = "port")
    int port();

}
