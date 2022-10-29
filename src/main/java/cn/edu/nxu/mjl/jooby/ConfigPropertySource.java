package cn.edu.nxu.mjl.jooby;

import com.typesafe.config.Config;
import io.micronaut.context.env.PropertySource;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

public class ConfigPropertySource implements PropertySource {

    private final Config config;

    public ConfigPropertySource(@Nonnull Config config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return "jooby";
    }

    @Override
    public Object get(String key) {
        return config.getAnyRef(key);
    }

    @Override
    public Iterator<String> iterator() {
        return config.entrySet().stream().map(Map.Entry::getKey).iterator();
    }
}
