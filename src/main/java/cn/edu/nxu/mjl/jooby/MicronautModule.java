package cn.edu.nxu.mjl.jooby;


import com.typesafe.config.Config;
import io.jooby.*;
import io.jooby.annotations.Path;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Map;

public class MicronautModule implements Extension {

    private ApplicationContext applicationContext;

    private boolean refresh = true;
    private boolean registerMvcRoutes = true;

    private boolean ignoreEntry(Class type) {
        return type == Environment.class || type == Config.class;
    }

    public MicronautModule noMvcRoutes() {
        this.registerMvcRoutes = false;
        return this;
    }

    public MicronautModule noRefresh() {
        this.refresh = false;
        return this;
    }

    public MicronautModule(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public MicronautModule() {
    }


    @Override
    public void install(@Nonnull Jooby application) throws Exception {


        Environment environment = application.getEnvironment();
        String[] profiles = environment.getActiveNames().toArray(new String[0]);
        if (applicationContext == null) {
            applicationContext = ApplicationContext.run(profiles);
        }
        Config config = environment.getConfig();

        io.micronaut.context.env.Environment configurableEnvironment = applicationContext.getEnvironment();

        configurableEnvironment.addPropertySource(new ConfigPropertySource(config));

        applicationContext.registerSingleton(Config.class, config);
        applicationContext.registerSingleton(Environment.class, environment);
        applicationContext.registerSingleton(io.micronaut.context.env.Environment.class, configurableEnvironment);

        ServiceRegistry registry = application.getServices();
        for (Map.Entry<ServiceKey<?>, Provider<?>> entry : registry.entrySet()) {
            ServiceKey key = entry.getKey();
            if (!ignoreEntry(key.getType())) {
                Provider provider = entry.getValue();
                applicationContext.registerSingleton(key.getType(), provider.get());
            }
        }


        application.registry(new MicronautRegistry(applicationContext));

        if (registerMvcRoutes) {
            Collection<BeanDefinition<?>> controllerBeanList = applicationContext
                    .getBeanDefinitions(Qualifiers.byStereotype(Path.class));
            ClassLoader loader = application.getClass().getClassLoader();
            controllerBeanList.forEach(bd -> {
                Object mvcClass = applicationContext.getBean(bd);
                application.mvc(mvcClass);
            });
        }

        if (refresh) {
            applicationContext.refresh();
        }
        application.onStop(applicationContext);


    }

}
