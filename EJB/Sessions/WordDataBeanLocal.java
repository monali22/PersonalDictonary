/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entities.Worddata3;
import java.util.List;
import javax.ejb.Local;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author kuldeep
 */
@Local
public interface WordDataBeanLocal {
    void save(String uname , String word, JSONArray meaning);
    
    List<Worddata3> getByWord(String word);
   
    List<JSONArray> getAll(String uname);
    
    List<String> getScores();
}
