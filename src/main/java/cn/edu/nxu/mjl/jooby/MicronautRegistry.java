package cn.edu.nxu.mjl.jooby;

import io.jooby.Registry;
import io.jooby.ServiceKey;
import io.jooby.exception.RegistryException;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.exceptions.NonUniqueBeanException;
import io.micronaut.inject.qualifiers.Qualifiers;

import javax.annotation.Nonnull;

public class MicronautRegistry implements Registry {
    private final ApplicationContext ctx;

    MicronautRegistry(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Nonnull
    @Override
    public <T> T require(@Nonnull Class<T> type) throws RegistryException {
        try {
            return ctx.getBean(type);
        } catch (NonUniqueBeanException ex) {
            throw new RegistryException("获取`" + type.getName() + "`失败,原因 ：", ex);
        }
    }

    @Nonnull
    @Override
    public <T> T require(@Nonnull Class<T> type, @Nonnull String name) throws RegistryException {
        try {
            return ctx.getBean(type, Qualifiers.byName(name));
        } catch (NonUniqueBeanException ex) {
            throw new RegistryException("获取`" + type.getName() + "`失败,原因 ：", ex);
        }
    }

    @Nonnull
    @Override
    public <T> T require(@Nonnull ServiceKey<T> key) throws RegistryException {
        String name = key.getName();
        return name == null ? require(key.getType()) : require(key.getType(), name);
    }
}
