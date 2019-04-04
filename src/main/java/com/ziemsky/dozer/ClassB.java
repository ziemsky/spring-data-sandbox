package com.ziemsky.dozer;

public class ClassB {

    private String textPropB;

    private ClassC classC;

    public ClassB(final String textPropB, final ClassC classC) {
        this.textPropB = textPropB;
        this.classC = classC;
    }

    private ClassB() {
    }

    @Override public String toString() {
        return "ClassB{" +
            "textPropB='" + textPropB + '\'' +
            ", classC=" + classC +
            '}';
    }
}
