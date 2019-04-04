package com.ziemsky.dozer;

public class ClassB {

    private String textPropB;

    private ClassC classC;

    public ClassB(final String textPropB, final ClassC classC) {
        this.textPropB = textPropB;
        this.classC = classC;
    }

    public ClassB() {
    }

    public void setTextPropB(final String textPropB) {
        this.textPropB = textPropB;
    }

    public void setClassC(final ClassC classC) {
        this.classC = classC;
    }

    public String getTextPropB() {
        return textPropB;
    }

    public ClassC getClassC() {
        return classC;
    }

    @Override public String toString() {
        return "ClassB{" +
            "textPropB='" + textPropB + '\'' +
            ", classC=" + classC +
            '}';
    }
}
