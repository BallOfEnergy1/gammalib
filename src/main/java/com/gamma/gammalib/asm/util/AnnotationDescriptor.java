package com.gamma.gammalib.asm.util;

import java.lang.annotation.Annotation;

public class AnnotationDescriptor {

    public Class<? extends Annotation> annotationClass;
    public Object[] keyValuePairs;

    public AnnotationDescriptor(Class<? extends Annotation> annotationClass, Object... keyValuePairs) {
        this.annotationClass = annotationClass;
        this.keyValuePairs = keyValuePairs;
    }
}
