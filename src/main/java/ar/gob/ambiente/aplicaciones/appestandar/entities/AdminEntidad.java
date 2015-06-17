/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.aplicaciones.appestandar.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rincostante
 */
@XmlRootElement(name = "adminentidad")
@Entity
@Table(name = "adminentidad")
public class AdminEntidad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usalta_id", nullable=false)
    @NotNull(message = "Debe haber un usuario dealta")
    private Usuario usAlta;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAlta;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usmodif_id", nullable=true)
    private Usuario usModif;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaModif;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usbaja_id", nullable=true)
    private Usuario usBaja;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaBaja;
    private boolean habilitado;

    /**
     * Campo que muestra la fecha de alta como string
     */
    @Transient
    String strFechaAlta;
    
    /**
     * Campo que muestra la fecha de baja como string
     */
    @Transient
    String strFechaModif;
    
    /**
     * Campo que muestra la fecha de baja como string
     */
    @Transient
    String strFechaBaja;
    
    public String getStrFechaBaja() {
        if(fechaBaja != null){
            SimpleDateFormat formateador = new SimpleDateFormat("dd'/'MM'/'yyyy", new Locale("es_ES"));
            strFechaBaja = formateador.format(fechaBaja);
            return strFechaBaja;
        }
        return "";
    }

    public void setStrFechaBaja(String strFechaBaja) {
        this.strFechaBaja = strFechaBaja;
    }

    /**
     * 
     * @return 
     */
    public String getStrFechaAlta() {
        SimpleDateFormat formateador = new SimpleDateFormat("dd'/'MM'/'yyyy", new Locale("es_ES"));
        strFechaAlta = formateador.format(fechaAlta);   
        return strFechaAlta;
    }

    public void setStrFechaAlta(String strFechaAlta) {
        this.strFechaAlta = strFechaAlta;
    }    
    
    public String getStrFechaModif(){
        if(fechaModif != null){
            SimpleDateFormat formateador = new SimpleDateFormat("dd'/'MM'/'yyyy", new Locale("es_ES"));
            strFechaModif = formateador.format(fechaModif);
            return strFechaModif;
        }
        return "";
    }
    
    public void setStrFechaModif(String strFechaModif){
        this.strFechaModif = strFechaModif;
    }
            
    public Usuario getUsAlta() {
        return usAlta;
    }

    public void setUsAlta(Usuario usAlta) {
        this.usAlta = usAlta;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Usuario getUsModif() {
        return usModif;
    }

    public void setUsModif(Usuario usModif) {
        this.usModif = usModif;
    }

    public Date getFechaModif() {
        return fechaModif;
    }

    public void setFechaModif(Date fechaModif) {
        this.fechaModif = fechaModif;
    }

    public Usuario getUsBaja() {
        return usBaja;
    }

    public void setUsBaja(Usuario usBaja) {
        this.usBaja = usBaja;
    }

    public Date getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Date fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AdminEntidad)) {
            return false;
        }
        AdminEntidad other = (AdminEntidad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.aplicaciones.appestandar.entities.AdminEntidad[ id=" + id + " ]";
    }
    
}
