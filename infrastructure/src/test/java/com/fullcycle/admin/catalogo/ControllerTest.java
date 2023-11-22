package com.fullcycle.admin.catalogo;

import java.lang.annotation.*;

import com.fullcycle.catalogo.infrastructure.configuration.ObjectMapperConfig;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@WebMvcTest
@Import(ObjectMapperConfig.class)
@Tag("integrationTest")
public @interface ControllerTest {

  @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
  Class<?>[] controllers() default {};
}
