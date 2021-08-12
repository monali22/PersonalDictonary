/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import entities.Worddata3;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author monali
 */
//Statefull bean since WordDataBean needs to be stored through each session.
@Stateful
public class WordDataBean implements WordDataBeanLocal {

    /*
    user, password port, host values to connect using Jsch.
    remoteFile gives the location on remote machine
    local is the location on local machine
     */
    String user = "monali22";
    String password = "";
    String host = "10.158.82.61";
    int port = 22;
    String remoteFile = "/home/NETID/monali22/DictonaryApp";
    String local = "/Users/kuldeep/Desktop/contract";
    String fileSeparator = System.getProperty("file.separator");

    /* Creates a json file with username in the local location and 
     transfers it to remote location
     saves the word information from the API call to the json file
    in json format
    */
    
    @Override
    public void save(String uname, String word, JSONArray meaning) {
        String filename = uname + ".json";
        Session session = createSession();
        String absoluteFilePath = fileSeparator + "Users" + fileSeparator + "kuldeep" + fileSeparator + "Desktop" + fileSeparator + filename;
        File file = new File(absoluteFilePath);
        
        try {
            if (file.createNewFile()) {
                System.out.println("File created at " + absoluteFilePath);
                System.out.println("Moving file to " + remoteFile);
                copyLocalToRemote(session, absoluteFilePath, remoteFile, filename);
            } else {
                System.out.println("File " + absoluteFilePath + " already exists");
            }
        } 
        catch (Exception e) {
        } 
        finally {
            String wordMean = meaning.toJSONString() + "\n";
            try {
                ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
                sftpChannel.connect();
                sftpChannel.cd(remoteFile);
                String dest = remoteFile + fileSeparator + file.getName();
                System.out.println("Writing to the file " + dest);
                System.out.println("Writing " + wordMean);
                Channel channel = session.openChannel("sftp");
                channel.connect();
                String checkword = "";
                boolean res = false;
                List<JSONArray> arr = getAll(uname);
                for (int j = 0; j < arr.size(); j++) {
                    JSONArray jSONArray = arr.get(j);
                    for (int i = 0; i < jSONArray.size(); i++) {
                        JSONObject jSONObject1 = (JSONObject) jSONArray.get(i);
                        String json2 = jSONObject1.get("meta").toString();
                        JSONParser parser = new JSONParser();
                        JSONObject jSONObject2 = null;
                        try {
                            jSONObject2 = (JSONObject) parser.parse(json2);
                        } catch (ParseException ex) {
                            Logger.getLogger(WordDataBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        checkword = jSONObject2.get("id").toString();
                        if (word.equals(checkword)) {
                            res = true;
                            break;
                        }
                    }
                }
                if (!res) {
                    sftpChannel.put(new ByteArrayInputStream(wordMean.getBytes()), dest, ChannelSftp.APPEND);
                }
            } catch (Exception e) {
                System.out.println("Exception occurred during reading file from SFTP server due to " + e.getMessage());
                e.getMessage();
            }
        }
    }
    
    
    @Override
    public List<Worddata3> getByWord(String word) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
     Reads all the words learned by the user in list of JSONArray
    */
    @Override
    public List<JSONArray> getAll(String uname) {
        List<JSONArray> list = new ArrayList<>();
        Session session = createSession();
        String dest = remoteFile + fileSeparator + uname + ".json";
        System.out.println("Reading to the file " + dest);
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            InputStream stream = sftpChannel.get(dest);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = br.readLine()) != null) {
                    JSONParser parser = new JSONParser();
                    JSONArray jSONArray = (JSONArray) parser.parse(line);
                    list.add(jSONArray);
                    System.out.println(line);
                }
            } catch (IOException io) {
                System.out.println("Exception occurred during reading file from SFTP server due to " + io.getMessage());
                io.getMessage();
            } catch (Exception e) {
                System.out.println("Exception occurred during reading file from SFTP server due to " + e.getMessage());
                e.getMessage();
            }
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
     Counts the number of words learned by each user
     Vector is used to collect the files from the remote location
    */
    @Override
    public List<String> getScores() {
        List<String> list = new ArrayList<>();
        HashMap<String,Integer> map = new HashMap<>();
        Session session = createSession();
        String dest = remoteFile + fileSeparator;
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            Vector<LsEntry> directoryEntries = sftpChannel.ls(dest);
            directoryEntries.remove(0); // to ignore the unnamed files
            directoryEntries.remove(1);
            for (LsEntry file : directoryEntries) {
                String userName = file.getFilename();
                int count = 0;
                String userFileDest = dest + userName;
                InputStream stream = sftpChannel.get(userFileDest);
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        count++;
                    }
                } catch (IOException io) {
                    
                    io.getMessage();
                } catch (Exception e) {
                    
                    e.getMessage();
                }
                map.put(userName,count);
                System.out.println(userName + " count is " + count);
            }
            map = sortByValue(map);
            for (Map.Entry<String, Integer> en : map.entrySet()) { 
                list.add(en.getKey()+":="+en.getValue());
            } 
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
    method to create remote session using Jsch
    */
    private Session createSession() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();
            return session;
        } catch (JSchException ex) {
            Logger.getLogger(WordDataBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     scp function is used in this method 
    to copy the file from local to remote machine
    */
    private void copyLocalToRemote(Session session, String local, String remoteFile, String filename) throws JSchException, IOException {
        boolean ptimestamp = true;
        local = local + File.separator + filename;
        String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + remoteFile;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();
        channel.connect();
        if (checkAck(in) != 0) {
            System.exit(0);
        }
        File _lfile = new File(local);
        if (ptimestamp) {
            command = "T" + (_lfile.lastModified() / 1000) + " 0";
            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }
        }

        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = _lfile.length();
        command = "C0644 " + filesize + " ";
        if (local.lastIndexOf('/') > 0) {
            command += local.substring(local.lastIndexOf('/') + 1);
        } else {
            command += local;
        }

        command += "\n";
        out.write(command.getBytes());
        out.flush();

        if (checkAck(in) != 0) {
            System.exit(0);
        }

        // send a content of lfile
        FileInputStream fis = new FileInputStream(local);
        byte[] buf = new byte[1024];
        while (true) {
            int len = fis.read(buf, 0, buf.length);
            if (len <= 0) {
                break;
            }
            out.write(buf, 0, len);
        }

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        if (checkAck(in) != 0) {
            System.exit(0);
        }
        out.close();

        try {
            if (fis != null) {
                fis.close();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        System.out.println("Moved file to " + remoteFile);
    }
    
    
    public static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //         -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }
    
    /* To sort the scores of the users according to the count
    */
    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, Integer> > list = 
               new LinkedList<Map.Entry<String, Integer> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
        for (Map.Entry<String, Integer> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 
}
