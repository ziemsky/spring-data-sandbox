package com.ziemsky.springdata;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.events.Event;
import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.ziemsky.springdata.jpa.entities.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DozerTest {

    private static final Logger log = LoggerFactory.getLogger(DozerTest.class);

    // final Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @Test
    void withBLevelMappingBuilder() {
        final Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
                @Override protected void configure() {

                    mapping(ClassBPrime.class, ClassB.class);

                }
            })
            // .withEventListener(new LoggingEventListener())
            .build();


        testWith(mapper);
    }

    private void testWith(final Mapper mapper) {
        final ClassA srcRoot =
            new ClassA("A",
                new ClassB("B",
                    new ClassC("C")));

        final ClassA targetRoot = mapper.map(srcRoot, ClassA.class);

        // SOURCE hierarchy
        assertThat(srcRoot.toString())
            .isEqualTo("ClassA{textPropA='A', classB=ClassB{textPropB='B', classC=ClassC{textPropC='C'}}}");

        // TARGET hierarchy
        assertThat(targetRoot.toString())
            .isEqualTo("ClassA{textPropA='A', classB=ClassBPrime{textPropB='B', classC=ClassC{textPropC='C'}}}");
    }

    static void log(Event event) {
        log.debug("EVENT: {}", event);
    }

    static class LoggingEventListener implements EventListener {
        @Override public void onMappingStarted(final Event event) { log(event); }

        @Override public void onPreWritingDestinationValue(final Event event) { log(event); }

        @Override public void onPostWritingDestinationValue(final Event event) { log(event); }

        @Override public void onMappingFinished(final Event event) { log(event); }
    }
}
