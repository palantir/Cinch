package com.palantir.ptoss.cinch;

import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializableModelTest {

    private static final Logger log = LoggerFactory.getLogger(SerializableModelTest.class);
    private static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    private final Binding testBinding = new Binding() {
        @Override
        public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
            log.info("Ran update");
        }
    };

    @Test
    public void testJavaSerialize() {
        SerializableBindableModel model = new SerializableBindableModel("testing");
        javaRoundTrip(model);
    }

    @Test
    public void testJavaSerializeWithBinding() {
        SerializableBindableModel model = new SerializableBindableModel("testing");
        model.bind(testBinding);
        model.update();
        SerializableBindableModel rehydrated = javaRoundTrip(model);
        rehydrated.bind(testBinding);
        rehydrated.update();
    }

    private SerializableBindableModel javaRoundTrip(SerializableBindableModel model) {
        SerializableBindableModel rehydrated = (SerializableBindableModel) SerializationUtils.clone(model);
        Assert.assertEquals(model, rehydrated);
        return rehydrated;
    }

    @Test
    public void testFstSerialize() {
        SerializableBindableModel model = new SerializableBindableModel("testing");
        fstRoundTrip(model);
    }

    @Test
    public void testFstSerializeWithBinding() {
        SerializableBindableModel model = new SerializableBindableModel("testing");
        model.bind(testBinding);
        model.update();
        SerializableBindableModel rehydrated = fstRoundTrip(model);
        rehydrated.bind(testBinding);
        rehydrated.update();
    }

    private SerializableBindableModel fstRoundTrip(SerializableBindableModel model) {
        byte[] bytes = conf.asByteArray(model);
        SerializableBindableModel rehydrated = (SerializableBindableModel)conf.asObject(bytes);
        Assert.assertEquals(model, rehydrated);
        return rehydrated;
    }
}
