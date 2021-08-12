/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kuldeep
 */
@Entity
@Table(name = "USERINO4")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Userino4.findAll", query = "SELECT u FROM Userino4 u")
    , @NamedQuery(name = "Userino4.findById", query = "SELECT u FROM Userino4 u WHERE u.id = :id")
    , @NamedQuery(name = "Userino4.findByUname", query = "SELECT u FROM Userino4 u WHERE u.uname = :uname")
    , @NamedQuery(name = "Userino4.findByPassword", query = "SELECT u FROM Userino4 u WHERE u.password = :password")
    , @NamedQuery(name = "Userino4.login", query = "SELECT u FROM Userino4 u WHERE u.password = :password and u.uname = :uname")
})
public class Userino4 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 30)
    @Column(name = "UNAME")
    private String uname;
    @Size(max = 30)
    @Column(name = "PASSWORD")
    private String password;

    public Userino4() {
    }

    public Userino4(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        if (!(object instanceof Userino4)) {
            return false;
        }
        Userino4 other = (Userino4) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Userino4[ id=" + id + " ]";
    }
    
}
