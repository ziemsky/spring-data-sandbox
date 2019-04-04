package com.ziemsky.springdata;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.events.Event;
import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.api.FieldsMappingOptions;
import com.github.dozermapper.core.loader.api.TypeMappingOptions;
import com.ziemsky.dozer.ClassA;
import com.ziemsky.dozer.ClassB;
import com.ziemsky.dozer.ClassBPrime;
import com.ziemsky.dozer.ClassC;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DozerTest {



    final Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    // final Mapper mapper = DozerBeanMapperBuilder.create()
        // .withMappingBuilder(new BeanMappingBuilder() {
        //     @Override protected void configure() {
        //         mapping(
        //             ClassB.class,
        //             ClassBPrime.class
        //             ,
        //             TypeMappingOptions.oneWay()
        //         )
        //             .fields(
        //                 field("textPropA"),
        //                 field("textPropBPrime").setMethod("setTextPropBPrime"),
        //                 FieldsMappingOptions.oneWay()
        //             );
        //
        //         mapping(ClassA.class, ClassA.class);
        //     }
        // })
        // .withEventListener(new LoggingEventListener())
        // .build();

    @Test
    void name() {
        final ClassA srcRoot =
            new ClassA("A",
                new ClassB("B",
                    new ClassC("C")));

        System.out.println("\n\n\nSOURCE:\n\n" + srcRoot + "\n\n\n");

        final ClassA targetRoot = mapper.map(srcRoot, ClassA.class);

        assertThat(targetRoot.toString())
            .isEqualTo("ClassA{textPropA='A', classB=ClassBPrime{textPropBPrime='B', classC=ClassC{textPropC='C'}}}");
    }

    static void log(Event event) {
        System.out.println("\n\n" + event);
    }

    static class LoggingEventListener implements EventListener {
        @Override public void onMappingStarted(final Event event) { log(event); }

        @Override public void onPreWritingDestinationValue(final Event event) { log(event); }

        @Override public void onPostWritingDestinationValue(final Event event) { log(event); }

        @Override public void onMappingFinished(final Event event) { log(event); }
    }
}
