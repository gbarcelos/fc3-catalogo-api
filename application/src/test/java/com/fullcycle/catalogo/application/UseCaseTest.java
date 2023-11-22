package com.fullcycle.catalogo.application;

import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unitTest")
public abstract class UseCaseTest implements BeforeEachCallback {
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        Mockito.reset(getMocks().toArray());
    }
    protected abstract List<Object> getMocks();
}
