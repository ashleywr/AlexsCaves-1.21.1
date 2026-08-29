package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class WebHelper {

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;

    private WebHelper() {
    }

    @Nullable
    public static BufferedReader getURLContents(@Nonnull String urlString, @Nonnull String backupFileLoc) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            // Without these the JDK defaults to no timeout at all, so an unreachable
            // host blocks a modloading worker until the OS gives up on the socket.
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            InputStream stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            return new BufferedReader(reader);
        } catch (Exception var7) {
            try {
                // WebHelper.class.getClass() is Class.class, whose loader is the
                // bootstrap loader, i.e. null, so this always threw and the bundled
                // fallback never actually loaded.
                InputStream backup = WebHelper.class.getClassLoader().getResourceAsStream(backupFileLoc);
                if (backup == null) {
                    AlexsCaves.LOGGER.warn("Could not download list of mod incompatibilities and no bundled copy was found at {}", backupFileLoc);
                    return null;
                }
                return new BufferedReader(new InputStreamReader(backup, StandardCharsets.UTF_8));
            } catch (NullPointerException var6) {
                AlexsCaves.LOGGER.warn("Could not download list of mod incompatibilities");
                return null;
            }
        }
    }
}
