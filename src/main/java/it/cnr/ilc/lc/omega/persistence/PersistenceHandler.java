/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
    private ThreadLocal<EntityManager> threadLocal = null;

    private static Logger log = LogManager.getLogger(PersistenceHandler.class);

    public synchronized EntityManager getEntityManager() {

        if (null == threadLocal) {
            threadLocal = new ThreadLocal<>();
        }

        EntityManager em = threadLocal.get();

        log.info(
                "entityManagerFactory is null? " + (null == entityManagerFactory));

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
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e);
        }
        return em;

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
