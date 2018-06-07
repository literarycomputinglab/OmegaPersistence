/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import sirius.kernel.di.std.Register;

/**
 *
 *
 */
@Register(classes = PersistenceHandler.class)
public class PersistenceHandler {

    //TODO http://stackoverflow.com/questions/14888040/java-an-entitymanager-object-in-a-multithread-environment
    private EntityManagerFactory entityManagerFactory = null;
    private ThreadLocal<EntityManager> threadLocal = null;
    
    private static Logger log = LogManager.getLogger(PersistenceHandler.class);
    
    {
        log.info("entityManagerFactory init()");
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("OmegaPU");
            
        } catch (CommandAcceptanceException cae) {
            log.warn(cae.getMessage());
        } catch (Exception e) {
            log.error(e);
        }
        log.info("entityManagerFactory " + entityManagerFactory);
        
    }
    
    public synchronized EntityManager getEntityManager() {
        
        log.info("threadLocal is " + threadLocal);
        if (null == threadLocal) {
            threadLocal = new ThreadLocal<>();
        }
        log.info("Tring to get the entityt manager...");
        
        EntityManager em = threadLocal.get();
        
        log.info("entityManagerFactory is null? " + (null == entityManagerFactory));
        
        if (null == entityManagerFactory) {
            entityManagerFactory = Persistence.createEntityManagerFactory("OmegaPU");
            log.info("entityManagerFactory is now open? " + entityManagerFactory.isOpen());
        } else if (!entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory("OmegaPU");
        }
        
        try {
            
            if (null == em || !em.isOpen()) {
                em = entityManagerFactory.createEntityManager();
                threadLocal.set(em);
            }
        } catch (Exception e) {
            log.error("In PersistenceHandler", e);
            throw new IllegalStateException(e);
        }
        return em;
        
    }
    
    public synchronized void close() {
        log.info("Closing Entity Manager Factory...");
        
        try {
            if (null != entityManagerFactory) {
                if (entityManagerFactory.isOpen()) {
                    entityManagerFactory.close();
                }
                log.info("on close(): entityManagerFactory.close(), entityManagerFactory is [" + entityManagerFactory + "]");
            } else {
                log.warn("on close(): entityManagerFactory is null!");
            }
        } catch (IllegalStateException e) {
            log.error("On close() entityManagerFactory: " + e.getMessage());
        }
        
        log.info("on close(): entityManagerFactory closed");
    }
    
}
