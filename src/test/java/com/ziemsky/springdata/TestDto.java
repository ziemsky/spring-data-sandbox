package com.ziemsky.springdata;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class TestDto {
    private Integer id;
    private String textProp;

    public TestDto() {
    }

    public TestDto(final Integer id, final String textProp) {
        this.id = id;
        this.textProp = textProp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getTextProp() {
        return textProp;
    }

    public void setTextProp(final String textProp) {
        this.textProp = textProp;
    }

    @Override public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
