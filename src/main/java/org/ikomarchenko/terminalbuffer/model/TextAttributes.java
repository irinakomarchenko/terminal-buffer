package org.ikomarchenko.terminalbuffer.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public final class TextAttributes {

    private final TerminalColor foreground;
    private final TerminalColor background;
    private final Set<TextStyle> styles;

    public TextAttributes(TerminalColor foreground, TerminalColor background, Set<TextStyle> styles) {
        this.foreground = Objects.requireNonNull(foreground, "foreground must not be null");
        this.background = Objects.requireNonNull(background, "background must not be null");
        this.styles = Collections
                .unmodifiableSet(EnumSet
                        .copyOf(Objects.requireNonNull(styles, "styles must not be null")
        ));
    }

    public static TextAttributes defaultAttributes() {
        return new TextAttributes(
                TerminalColor.DEFAULT,
                TerminalColor.DEFAULT,
                EnumSet.noneOf(TextStyle.class)
        );
    }

    public TerminalColor getForeground() {
        return foreground;
    }

    public TerminalColor getBackground() {
        return background;
    }

    public Set<TextStyle> getStyles() {
        return styles;
    }

    public boolean hasStyle(TextStyle style) {
        return styles.contains(style);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextAttributes that)) {
            return false;
        }
        return foreground == that.foreground
                && background == that.background
                && styles.equals(that.styles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foreground, background, styles);
    }

    @Override
    public String toString() {
        return "TextAttributes{" +
                "foreground=" + foreground +
                ", background=" + background +
                ", styles=" + styles +
                '}';
    }
}
