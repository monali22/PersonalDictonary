/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entities.Userino4;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author monali
 */
@Local
public interface UserInfoBeanLocal {
    void signUp(String username, String password);
    
    boolean getByUname(String uname);
    
    boolean login(String username, String password);
    
    int getId(String uname);
}
