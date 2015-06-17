/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.aplicaciones.appestandar.facades;

import ar.gob.ambiente.aplicaciones.appestandar.entities.Rol;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rincostante
 */
@Stateless
public class RolFacade extends AbstractFacade<Rol> {
    @PersistenceContext(unitName = "appEstandar_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RolFacade() {
        super(Rol.class);
    }
    
    /**
     * Metodo que verifica si ya existe la entidad.
     * @param nombre, nombre del Rol a buscar
     * @return: devuelve True o False
     */
    public boolean existe(String nombre){
        em = getEntityManager();       
        String queryString = "SELECT rol.nombre FROM Rol rol "
                + "WHERE rol.nombre = :nombre ";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        return q.getResultList().isEmpty();
    }      
    
    /**
     * Método que verifica si la entidad tiene dependencia (Hijos) en estado HABILITADO
     * @param id: ID de la entidad
     * @return: True o False
     */
    public boolean noTieneDependencias(Long id){
        em = getEntityManager();       
        String queryString = "SELECT usu FROM Usuario usu " 
                + "WHERE usu.rol.id = :id "
                + "AND usu.adminentidad.habilitado = true";      
        Query q = em.createQuery(queryString)
                .setParameter("id", id);
        
        return q.getResultList().isEmpty();
    }    
    
   /**
     * Método que devuelve un LIST con las entidades HABILITADAS
     * @return: True o False
     */
    public List<Rol> getActivos(){
        em = getEntityManager();        
        List<Rol> result;
        String queryString = "SELECT rol FROM Rol rol " 
                + "WHERE rol.adminentidad.habilitado = true";                   
        Query q = em.createQuery(queryString);
        result = q.getResultList();
        return result;
    }     
}
