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
import javax.persistence.Lob;
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
@Table(name = "WORDDATA3")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Worddata3.findAll", query = "SELECT w FROM Worddata3 w")
    , @NamedQuery(name = "Worddata3.findById", query = "SELECT w FROM Worddata3 w WHERE w.id = :id")
    , @NamedQuery(name = "Worddata3.findByWord", query = "SELECT w FROM Worddata3 w WHERE w.word = :word")})
public class Worddata3 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 80)
    @Column(name = "WORD")
    private String word;
    @Lob
    @Size(max = 32700)
    @Column(name = "MEANING")
    private String meaning;

    public Worddata3() {
    }

    public Worddata3(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
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
        if (!(object instanceof Worddata3)) {
            return false;
        }
        Worddata3 other = (Worddata3) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Worddata3[ id=" + id + " ]";
    }

    public void setUname(String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
