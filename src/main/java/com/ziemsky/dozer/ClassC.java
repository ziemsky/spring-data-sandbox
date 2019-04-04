package com.ziemsky.dozer;

public class ClassC {
    private String textPropC;

    private ClassC() {
    }

    public ClassC(final String textPropC) {
        this.textPropC = textPropC;
    }

    @Override public String toString() {
        return "ClassC{" +
            "textPropC='" + textPropC + '\'' +
            '}';
    }
}
