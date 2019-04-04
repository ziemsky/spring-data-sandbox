package com.ziemsky.springdata;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.ziemsky.dozer.ClassA;
import com.ziemsky.dozer.ClassB;
import com.ziemsky.dozer.ClassC;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DozerTest {

    Mapper mapper = DozerBeanMapperBuilder.create()
        .build();

    @Test
    void name() {
        final ClassA srcRoot = new ClassA("A",
            new ClassB("B",
                new ClassC("C")));

        System.out.println("\n\n\nSOURCE:\n\n" + srcRoot);

        final ClassA targetRoot = mapper.map(srcRoot, ClassA.class);

        assertThat(targetRoot.toString()).isEqualTo("ClassA{textPropA='A', classB=ClassBPrime{textPropBPrime='B', classC=ClassC{textPropC='C'}}}");
    }
}
