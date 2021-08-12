/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entities.Userino4;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author monali
 */
@Stateless
public class UserInfoBean implements UserInfoBeanLocal {

    @PersistenceContext
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }
    
    private List<Userino4> userinfo2;
    
    @PostConstruct
    public void init() {
        userinfo2 = new ArrayList<>();
    }
    
    // Method to sign Up the user
    // This method generated a random id for each user.
    // The id is the primary key in the userinfo table.
    // It assigns the username and password to the Userinfo object 
    // and saves in the table.
    
    @Override
    public void signUp(String username, String password) {
        int idd = (int) Math.floor(Math.random() * 100);
        Userino4 u = new Userino4();
        u.setId(idd);
        u.setUname(username);
        u.setPassword(password);
        em.persist(u);
        System.out.println(" SignedUp user " + username);
    }

    @Override
    public boolean getByUname(String uname) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    // Method to log in the user
    // This method creates a query for the userinfo table
    // using the given username and password.
    // If the user exists in the table it returns true 
    // else it returns false.
    
    @Override
    public boolean login(String username, String password) {
        javax.persistence.Query q = em.createNamedQuery("Userino4.login");
        q.setParameter("uname", username);
        q.setParameter("password", password);
        if (q.getResultList().size() == 0)
        {
            System.out.println("Not found user " + username);
            return false;    
        }   
        else {
            System.out.println("Found  user " + username);
            return true; 
        }
    }
    
    // Method to get the id of the user
    // This method creates a query for the userinfo table
    // using the given username.
    // and returns the id of the given user
    
    @Override
    public int getId(String uname) {
        javax.persistence.Query q = em.createNamedQuery("Userino4.findByUname");
            q.setParameter("uname", uname);
        userinfo2 =  q.getResultList();
        int id = userinfo2.get(0).getId();
        return id;
    }

    

    
}
