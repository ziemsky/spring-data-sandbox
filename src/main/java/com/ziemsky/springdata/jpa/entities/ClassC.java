package com.ziemsky.springdata.jpa.entities;

public class ClassC {
    private String textPropC;

    public ClassC() {
    }

    public ClassC(final String textPropC) {
        this.textPropC = textPropC;
    }

    public String getTextPropC() {
        return textPropC;
    }

    public void setTextPropC(final String textPropC) {
        this.textPropC = textPropC;
    }

    @Override public String toString() {
        return "ClassC{" +
            "textPropC='" + textPropC + '\'' +
            '}';
    }
}
