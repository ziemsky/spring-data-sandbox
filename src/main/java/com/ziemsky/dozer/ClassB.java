package com.ziemsky.dozer;

public class ClassB extends AbstractClassB {

    private String textPropB;


    public ClassB(final String textPropB, final ClassC classC) {
        super(classC);
        this.textPropB = textPropB;
    }

    public ClassB() {
    }

    public void setTextPropB(final String textPropB) {
        this.textPropB = textPropB;
    }

    public String getTextPropB() {
        return textPropB;
    }

    @Override public String toString() {
        return "ClassB{" +
            "textPropB='" + textPropB + '\'' +
            ", classC=" + classC +
            '}';
    }
}
