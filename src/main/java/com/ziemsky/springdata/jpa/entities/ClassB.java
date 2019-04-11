package com.ziemsky.springdata.jpa.entities;

public class ClassB extends AbstractClassB {

    private String textPropB;

    public ClassB() {
    }

    public ClassB(final String textPropB, final ClassC classC) {
        super(classC);
        this.textPropB = textPropB;
    }

    public void setTextPropB(final String textPropB) {
        this.textPropB = textPropB;
    }

    @Override public String getTextPropB() {
        return textPropB;
    }

    @Override public String toString() {
        return "ClassB{" +
            "textPropB='" + textPropB + '\'' +
            ", classC=" + classC +
            '}';
    }
}
