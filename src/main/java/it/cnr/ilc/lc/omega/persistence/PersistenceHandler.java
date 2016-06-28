/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Register;

/**
 *
 *
 */
@Register(classes = PersistenceHandler.class)
public class PersistenceHandler {

    //TODO http://stackoverflow.com/questions/14888040/java-an-entitymanager-object-in-a-multithread-environment
    private EntityManagerFactory entityManagerFactory = null;

    private static Logger log = LogManager.getLogger(PersistenceHandler.class);

    private EntityManager manager = null;
            
    public synchronized EntityManager getEntityManager() {

        log.info("entityManagerFactory is null? " + (null == entityManagerFactory));

        if (null == entityManagerFactory) {
            entityManagerFactory = Persistence.createEntityManagerFactory("OmegaPU");
            log.info("entityManagerFactory is now open? " + entityManagerFactory.isOpen());
        } else if (!entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory("OmegaPU");
        }
        try {
            if (null == manager || !manager.isOpen()) {
                manager = entityManagerFactory.createEntityManager();
            } 
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e);
        }
        return manager;

    }

    public synchronized void close() {

        try {
            if (null != entityManagerFactory) {
                entityManagerFactory.close();
                log.info("entityManagerFactory.close()");
            } else {
                log.warn("entityManagerFactory is null!");
            }
        } catch (IllegalStateException e) {
            log.error("On close entityManagerFactory: " + e.getMessage());
        }

        log.info("entityManagerFactory closed");
    }

}
