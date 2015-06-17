/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.aplicaciones.appestandar.mb;

import ar.gob.ambiente.aplicaciones.appestandar.entities.AdminEntidad;
import ar.gob.ambiente.aplicaciones.appestandar.entities.Rol;
import ar.gob.ambiente.aplicaciones.appestandar.entities.Usuario;
import ar.gob.ambiente.aplicaciones.appestandar.facades.RolFacade;
import ar.gob.ambiente.aplicaciones.appestandar.facades.UsuarioFacade;
import ar.gob.ambiente.aplicaciones.appestandar.util.JsfUtil;
import ar.gob.ambiente.aplicaciones.appestandar.wsExt.Aplicacion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import ar.gob.ambiente.aplicaciones.appestandar.wsExt.AccesoAppWebService_Service;
import ar.gob.ambiente.aplicaciones.appestandar.wsExt.AccesoAppWebService;
import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author rincostante
 */
public class MbUsuario implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/AccesoAppWebService/AccesoAppWebService.wsdl")
    private AccesoAppWebService_Service service;
    
    private Usuario current;
    private List<Usuario> listado;
    private List<Usuario> listFilter;
    private Usuario usLogeado;
    private MbLogin login;   
    private boolean iniciado;  
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    
    @EJB
    private RolFacade rolFacade;
    @EJB
    private UsuarioFacade usuarioFacade;       
    private List<Rol> listaRol;
    
    private List<Aplicacion> lstApp;
    private Long idApp;
    private List<String> lstUsDisponibles; //Lista de pares usNombre - usNombreCompleto disponibles para poblar el combo
    private String usSeleccionado; //String con los datos del usuario usNombre - usNombreCompleto
    private static final Logger logger = Logger.getLogger(Usuario.class.getName());    

    /**
     * Creates a new instance of MbUsuario
     */
    public MbUsuario() {
    }
    
    /**
     *
     */
    @PostConstruct
    public void init(){
        lstUsDisponibles = new ArrayList();
        iniciado = false;
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        login = (MbLogin)ctx.getSessionMap().get("mbLogin");
        usLogeado = login.getUsLogeado();    
        lstApp = verAplicaciones();
    }    
    
    /**
     * Método que borra de la memoria los MB innecesarios al cargar el listado 
     */
    public void iniciar(){
        if(!iniciado){
            String s;
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(true);
            Enumeration enume = session.getAttributeNames();
            while(enume.hasMoreElements()){
                s = (String)enume.nextElement();
                if(s.substring(0, 2).equals("mb")){
                    if(!s.equals("mbLogin")){
                        session.removeAttribute(s);
                    }
                }
            }
        }
    }      
    
    /********************************
     ** Getters y Setters *********** 
     ********************************/
    
    public String getUsSeleccionado() {
        return usSeleccionado;
    }

    public void setUsSeleccionado(String usSeleccionado) {
        this.usSeleccionado = usSeleccionado;
    }

    
    public List<String> getLstUsDisponibles() {
        return lstUsDisponibles;
    }

    public void setLstUsDisponibles(List<String> lstUsDisponibles) {
        this.lstUsDisponibles = lstUsDisponibles;
    }

    public Usuario getCurrent() {
        return current;
    }

    public void setCurrent(Usuario current) {
        this.current = current;
    }
    
    public List<Rol> getListaRol() {
        return listaRol;
    }

    public void setListaRol(List<Rol> listaRol) {
        this.listaRol = listaRol;
    }
    
    public List<Usuario> getListado() {
        if(listado == null){
            listado = getFacade().findAll();
        }
        return listado;
    }

    public void setListado(List<Usuario> listado) {
        this.listado = listado;
    }

    public List<Usuario> getListFilter() {
        return listFilter;
    }

    public void setListFilter(List<Usuario> listFilter) {
        this.listFilter = listFilter;
    }
    
    /********************************
     ** Métodos de inicialización ***
     ********************************/    
    
    /**
     * @return La entidad gestionada
     */
    public Usuario getSelected() {
        if (current == null) {
            current = new Usuario();
        }
        return current;
    }  
    
    /*
     * Método para inicializar el listado de los Usuarios habilitados
     * @return acción para el listado de entidades
     */
    public String prepareList() {
        iniciado = true;
        return "list";
    }    
    
    /**
     * @return acción para el detalle de la entidad
     */
    public String prepareView() {
        return "view";
    }  
    
    /**
     * @return acción para la edición de la entidad
     */
    public String prepareEdit() {
        // cargo los roles
        listaRol = rolFacade.getActivos();
        update = 0;
        return "edit";        
    }    
    
    /**
     * @return acción para el formulario de nuevo
     */
    public String prepareCreate() {
        // inicializo variables
        idApp = Long.valueOf(0);      
        boolean usIngresa;
        String us;
        
        // obetengo la app        
        Iterator itApp = lstApp.listIterator();
        while(itApp.hasNext()){
            Aplicacion app = (Aplicacion)itApp.next();
            if(app.getUrl().equals(ResourceBundle.getBundle("/Bundle").getString("RutaAplicacion"))){
                idApp = app.getId();
            }
        }
        
        // obtengo los usuarios existentes
        List<Usuario> lstUsExist = usuarioFacade.findAll();
        
        // obtengo los usuarios disponibles para la app, solo si obtuve el idApp
        if(idApp > 0){
            List<ar.gob.ambiente.aplicaciones.appestandar.wsExt.Usuario> lstUsDisp = verUsuariosPorIdApp();
            Iterator itUsDisp = lstUsDisp.listIterator();
            while(itUsDisp.hasNext()){
                ar.gob.ambiente.aplicaciones.appestandar.wsExt.Usuario usDisp = (ar.gob.ambiente.aplicaciones.appestandar.wsExt.Usuario)itUsDisp.next();
                Iterator itUsExist = lstUsExist.listIterator();
                usIngresa = true;
                while(itUsExist.hasNext()){
                    Usuario usExist = (Usuario)itUsExist.next();
                    if(usExist.getNombre().equals(usDisp.getNombre())){
                        usIngresa = false;
                    }
                }
                if(usIngresa){
                    // agrego el usuario a la lista 
                    us = usDisp.getNombre() + "-" + usDisp.getNombreCompleto();
                    lstUsDisponibles.add(us);
                }
            }
        }

        // cargo los roles
        listaRol = rolFacade.getActivos();
        
        // creo la entidad
        current = new Usuario();
        
        return "new";
    }    
    
    /**
     *
     * @return
     */
    public String prepareInicio(){
        recreateModel();
        return "/faces/index";
    }
    
        /**
     * Método para preparar la búsqueda
     * @return la ruta a la vista que muestra los resultados de la consulta en forma de listado
     */
    public String prepareSelect(){
        return "list";
    }
    
         /**
     * @return mensaje que notifica la actualizacion de estado
     */    
    public String habilitar() {
        current.getAdmin().setHabilitado(true);
        update();        
        recreateModel();
        return "view";
    } 
    
    public String deshabilitar() {
        if (getFacade().noTieneDependencias(current.getId())){
            current.getAdmin().setHabilitado(false);
            update();        
            recreateModel();
        }
        else{
            //No Deshabilita 
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioNonDeletable"));            
        }
        return "view";
    } 
    
    /**
     * 
     */
    public void prepareHabilitar(){
        update = 2;
        update();        
    }
    
    /**
     * 
     */
    public void prepareDesHabilitar(){
        update = 1;
        update();        
    }        
    
    /*************************
    ** Métodos de operación **
    **************************/
    /**
     * Méto que inserta uno nuevo Usuario en la base de datos, previamente genera una entidad de administración
     * con los datos necesarios y luego se la asigna a la persona
     * @return mensaje que notifica la inserción
     */
    public String create() {
        try {
            // Creación de la entidad de administración y asignación
            Date date = new Date(System.currentTimeMillis());
            AdminEntidad admEnt = new AdminEntidad();
            admEnt.setFechaAlta(date);
            admEnt.setHabilitado(true);
            admEnt.setUsAlta(usLogeado);
            current.setAdmin(admEnt);

            // asigno los datos del usuario seleccionado para persistirlo
            current.setNombre(usSeleccionado.substring(0, usSeleccionado.indexOf("-")));
            current.setNombreCompleto(usSeleccionado.substring(usSeleccionado.indexOf("-") + 1, usSeleccionado.length()));

            // Inserción
            getFacade().create(current);
            recreateModel();
            return "view";

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("UsuarioCreatedErrorOccured"));
            return null;
        }
    }    
    
    /**
     * Método que actualiza un nuevo Docente en la base de datos.
     * Previamente actualiza los datos de administración
     * @return mensaje que notifica la actualización
     */
    public String update() {    
        try {
            // Actualización de datos de administración de la entidad según el valor de update
            Date date = new Date(System.currentTimeMillis());
            if(update == 1){
                current.getAdmin().setFechaBaja(date);
                current.getAdmin().setUsBaja(usLogeado);
                current.getAdmin().setHabilitado(false);
            }if(update == 2){
                current.getAdmin().setFechaModif(date);
                current.getAdmin().setUsModif(usLogeado);
                current.getAdmin().setHabilitado(true);
                current.getAdmin().setFechaBaja(null);
                current.getAdmin().setUsBaja(usLogeado);
            }if(update == 0){
                current.getAdmin().setFechaModif(date);
                current.getAdmin().setUsModif(usLogeado);
            }

            // Actualizo
            if(update == 0){
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
                return "view";
            }else if(update == 1){
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioDeshabilitado"));
                return "view";
            }else{
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioHabilitado"));
                return "view";
            }
                
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdatedErrorOccured"));
            return null;
        }
    }    
    
    /**
     * @param id equivalente al id de la entidad persistida
     * @return la entidad correspondiente
     */
    public Usuario getUsuario(java.lang.Long id) {
        return getFacade().find(id);
    }    
    
    
    /*********************
    ** Métodos privados **
    **********************/
    /**
     * @return el Facade
     */
    private UsuarioFacade getFacade() {
        return usuarioFacade;
    }    
    
    /**
     * Restea la entidad
     */
    private void recreateModel() {
        listado.clear();
        listado = null;
        lstUsDisponibles.clear();
        usSeleccionado = "";
    }      
    
    private List<Aplicacion> verAplicaciones() {
        List<Aplicacion> result;
        try{
            AccesoAppWebService port = service.getAccesoAppWebServicePort();
            return port.verAplicaciones();
        }catch(Exception ex){
            result = null;
            // muestro un mensaje al usuario
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioAppErrorWs"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("UsuarioAppErrorWs"), ex.getMessage()});
        }
        return result;
    }

    private List<ar.gob.ambiente.aplicaciones.appestandar.wsExt.Usuario> verUsuariosPorIdApp(){
        List<ar.gob.ambiente.aplicaciones.appestandar.wsExt.Usuario> result;
        try{
            AccesoAppWebService port = service.getAccesoAppWebServicePort();
            return port.verUsuariosPorApp(idApp);
        }catch(Exception ex){
            result = null;
            // muestro un mensaje al usuario
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioUsErrorWs"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("UsuarioUsErrorWs"), ex.getMessage()});
        }
        return result;
    }    

    
    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        /**
         *
         * @param facesContext
         * @param component
         * @param value
         * @return
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbUsuario controller = (MbUsuario) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbUsuario");
            return controller.getUsuario(getKey(value));
        }

        
        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }
        
        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        /**
         *
         * @param facesContext
         * @param component
         * @param object
         * @return
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Usuario) {
                Usuario o = (Usuario) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Usuario.class.getName());
            }
        }
    }              
}
