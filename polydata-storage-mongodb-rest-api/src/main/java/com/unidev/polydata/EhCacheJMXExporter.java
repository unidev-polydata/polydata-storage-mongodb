package com.unidev.polydata;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.management.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "net.javalib.isb.ehcache.jmx", matchIfMissing = true)
@ConditionalOnClass({Ehcache.class, MBeanServer.class})
public class EhCacheJMXExporter {

    @Autowired(required = false)
    private MBeanServer mbeanServer;
    @Autowired(required = false)
    private CacheManager cacheManager;

    @PostConstruct
    public void init() {
        if (mbeanServer == null || cacheManager == null) {
            return;
        }
        ManagementService.registerMBeans(cacheManager, mbeanServer, true, true, true, true, true);
    }

}