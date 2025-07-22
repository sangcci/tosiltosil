package tosiltosil.backend.module.category.domain.value;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryColor {
    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    MINT("mint"),
    SKYBLUE("skyblue"),
    BLUE("blue"),
    PURPLE("purple"),
    PINK("pink"),
    CORAL("coral"),
    INDIGO("indigo"),;

    private final String value;

    CategoryColor(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
