package com.example.java_lms_group_01.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Loads a profile image from a local path or web URL into an ImageView.
 */
public final class ProfileImageUtil {

    private ProfileImageUtil() {
    }

    public static void loadImage(ImageView imageView, String imagePath) {
        if (imageView == null) {
            return;
        }

        imageView.setImage(null);
        if (imagePath == null || imagePath.trim().isBlank()) {
            return;
        }

        try {
            String trimmed = imagePath.trim();
            String source = trimmed;
            if (!isWebPath(trimmed) && !trimmed.startsWith("file:")) {
                source = Path.of(trimmed).toUri().toString();
            }
            Image image = new Image(source, true);
            if (!image.isError()) {
                imageView.setImage(image);
            }
        } catch (Exception ignored) {
            imageView.setImage(null);
        }
    }

    private static boolean isWebPath(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }
}
