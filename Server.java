/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my_messenger;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.text.StyleConstants;

/**
 *
 
 */
public class Server {
    DataInputStream cl_din[],din;
DataOutputStream cl_dout[],dout;
ArrayList<DataOutputStream> out_list;
ArrayList<Socket> client_list;
String uname,tmppath;
boolean is_server;
int active_cl=0,my_id;
Socket clients[],s;
ServerSocket ss;
    Server()
    {
       out_list= new ArrayList<DataOutputStream>(10);
client_list= new ArrayList<Socket>(10);
// chat_area.setContentType("text/html");
 //chat_area.setEditorKit(new HTMLEditorKit());

        clients=new  Socket[10];
        cl_din=new DataInputStream[10];
     //   cl_dout=new DataOutputStream[10];
        
    }
    boolean connect_Server(int port,String uname)
    {
        this.uname=uname;
        try{
             ss= new ServerSocket(port);   
       
              this.wait_clients();
      Main_Frame.set_chat_text("Server Connected at "+ InetAddress.getLocalHost().getHostAddress()+":"+ss.getLocalPort(), Color.red, port);
            
         }
        catch(Exception e){JOptionPane.showMessageDialog(null, e);
    return false;
}
        return true;
    }
    void wait_clients()
    {
         new Thread("server_listen"){
public void run()
{      while(!(ss.isClosed()))
       {
           try{
              s=ss.accept();
              active_cl=client_list.size();
               client_list.add(active_cl,s); 
               
               
// clients[active_cl]=ss.accept();
din = new DataInputStream(client_list.get(active_cl).getInputStream());
//cl_din[active_cl] = new DataInputStream(clients[active_cl].getInputStream());
//cl_dout[active_cl] = new DataOutputStream(clients[active_cl].getOutputStream());
out_list.add(active_cl,new DataOutputStream(client_list.get(active_cl).getOutputStream()));
server_listen(din,client_list.get(active_cl));
//JOptionPane.showMessageDialog(null, active_cl);
//cl_dout[active_cl].writeInt(active_cl);
out_list.get(active_cl).writeInt(active_cl);
//JOptionPane.showMessageDialog(null, active_cl);
//cl_dout[active_cl].flush();
out_list.get(active_cl).flush();
Main_Frame.set_chat_text("New User From "+s.getInetAddress().getHostName()+" Connected", Color.red, my_id);
send_to_all(-1,"New User From "+s.getInetAddress().getHostName()+" Connected");
s=null;
       }catch(Exception e){JOptionPane.showMessageDialog(null, e);
      
       }
       }
}}.start();
    }
    
     void send_to_all_image(int sid,String str2,File ff)
{
     for (int i=0;i<out_list.size();i++)
    {
       
     //  JOptionPane.showMessageDialog(null, i);
        
        if(i==sid || out_list.get(i)==null) continue;
        
        try{
     //   JOptionPane.showMessageDialog(null, i);
            DataOutputStream dos=(DataOutputStream)out_list.get(i);
      
    dos.writeInt(2);  
    dos.flush();
    dos.writeUTF(str2);
    dos.flush();
  //   JOptionPane.showMessageDialog(null, i);
InputStream fis=new FileInputStream(ff);
// JOptionPane.showMessageDialog(null, fis.available());
dos.writeUTF(ff.getName());
dos.flush();
dos.writeLong(ff.length()); 
dos.flush();
byte b[]=new byte[8192];
         int cnt;
               //  JOptionPane.showMessageDialog(this, "server :"+i+":"+ff.length());

  while( (cnt= fis.read(b))>0)
       {
       //    JOptionPane.showMessageDialog(this, "server :"+i+":"+cnt);
           dos.write(b, 0, cnt);
       }
    dos.flush();
       //     JOptionPane.showMessageDialog(this, "server :"+i+":sent");
    
    fis.close();
        dos=null;
        }
        catch(Exception e){ JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    
}
     void send_to_all(int sid,String str2)
{
   /* for(int i=1;i<active_cl;i++)
    {
        if(i==sid) continue;
        try{
        cl_dout[i].writeUTF(str2);
        cl_dout[i].flush();
        }
        catch(Exception e){ JOptionPane.showMessageDialog(this, e);
        }
    }*/
    
    for (int i=0;i<out_list.size();i++)
    {
        //c
       
        if(i==sid || out_list.get(i)==null) continue;
        try{
             DataOutputStream dos=(DataOutputStream)out_list.get(i);
       dos.writeInt(0);
        dos.flush();
             dos.writeUTF(str2);
        dos.flush();
        dos=null;
        }
        catch(Exception e){ JOptionPane.showMessageDialog(null, e);
        }
        
    }
}
    private void server_listen(final DataInputStream din,final Socket s)
    {
        new Thread("server_read"){
public void run()
{ String str2="";
  int cl_no=active_cl;
    while(!(s.isClosed()))
{
  
try{
     int mid=din.readInt();
     int sid=din.readInt();
      str2=din.readUTF();
//chat_area.setText(chat_area.getText()+"\n"+str2);
   
  
      if(mid==0)
        {     
            Main_Frame.set_chat_text(str2,Color.red,StyleConstants.ALIGN_LEFT);
            send_to_all(sid,str2);
        }
        else if(mid==1)
        { Main_Frame.set_chat_text(str2, Color.red, sid);
            send_to_all(sid,str2);
            out_list.get(sid).writeInt(1);
             out_list.get(sid).close();
            out_list.set(sid,null);
            client_list.get(sid).close();
            //client_list.remove(sid);
           
            
        }
           else if(mid==2)
         {
         
             String filename=din.readUTF();
             long length=din.readLong(); 
            File f=new File(tmppath+"\\"+uname);
             if(!f.isDirectory()) f.mkdirs();
             f=new File(f.getAbsoluteFile()+"\\"+filename);
             OutputStream fis=new FileOutputStream(f);
         byte b[]=new byte[8192];
         int cnt,total=0;
  while( (cnt= din.read(b))>0)
       {
           fis.write(b, 0, cnt);
           total=total+cnt;
           if(total==length) break;
       }
    fis.close(); 
   
             
             
        
          
             Main_Frame.set_chat_image(str2,Color.red,StyleConstants.ALIGN_LEFT,f);
             send_to_all_image(sid,str2,f);
         }
}
catch(Exception ee){JOptionPane.showMessageDialog(null, ee);}

        }
}
}.start();
    }
     void  cleanClose()
{
            
       
     //   JOptionPane.showMessageDialog(this, "ea");
       try{
              if(ss!=null )
                   {
                   if( !(ss.isClosed()))   
                   {
                       Main_Frame.set_chat_text("Server Closed...BYE Bye", Color.red, my_id);
                     send_to_all(-1,"Server Closed...BYE Bye");
                   for (int i=0;i<client_list.size();i++)
                         {
       
                       
                          if(out_list.get(i)==null) continue;
                          out_list.get(i).writeInt(1);
                           out_list.get(i).flush();
                           out_list.get(i).close();
                            client_list.get(i).close();
                            
                          }
        ss.close();
         out_list.clear();
            client_list.clear();
             
                   }
        
                   }
            
            
           }
     
        
        catch(Exception ee){ JOptionPane.showMessageDialog(null, ee);}
       
        
       
}
}
