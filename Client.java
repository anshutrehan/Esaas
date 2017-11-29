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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.text.StyleConstants;

/**
 *
 
 */
public class Client {
    DataInputStream din;
DataOutputStream dout;

String uname,tmppath;
boolean is_server;
int my_id;
Socket s;
    Client()
    {
     tmppath=System.getProperty("java.io.tmpdir");
      
    }
     boolean connect_client(String adress,int port,String uname)
    {
            
     
       this.uname=uname;
       try{
        s = new Socket(adress,port);
        
din = new DataInputStream(s.getInputStream());
dout = new DataOutputStream(s.getOutputStream());
my_id=din.readInt();

       this.client_listen();
       }
       catch(Exception ee){JOptionPane.showMessageDialog(null, ee);
       return false;
       }
    return true;
    }
  void client_listen()
  {
     try{
         new Thread("client_read"){
public void run()
{ String str2="";
    while(!(s.isClosed()))
{
try{
    int mid=din.readInt();
        if(mid==0)
        {
            str2=din.readUTF();
           // chat_area.setText(chat_area.getText()+"\n"+str2);
        Main_Frame.set_chat_text(str2,Color.red,StyleConstants.ALIGN_LEFT);
                }
        else if(mid==1)
        {
            s.close();
            break;
        }
         else if(mid==2)
         {
             str2=din.readUTF();
              String filename=din.readUTF();
             long length=din.readLong(); 
             File f=new File(tmppath+"\\"+uname);
             if(!f.isDirectory()) f.mkdirs();
             f=new File(f.getAbsoluteFile()+"\\"+filename);
             OutputStream fis=new FileOutputStream(f);
         byte b[]=new byte[8192];
         int cnt,total=0;
               //  JOptionPane.showMessageDialog(null, "client:"+my_id+":"+length);

  while( (cnt= din.read(b))>0)
       {
           fis.write(b, 0, cnt);
           total=total+cnt;
                 //   JOptionPane.showMessageDialog(null, "client :"+my_id+":"+cnt);

           if(total==length) break;
       }
    fis.close(); 
          //  JOptionPane.showMessageDialog(null, "client :"+my_id+":recieved");
           Main_Frame.set_chat_image(str2,Color.red,StyleConstants.ALIGN_LEFT,f);
             
         }
      
   }
catch(Exception ee){JOptionPane.showMessageDialog(null, ee);}

    
}
}
}.start();

    }                                          
catch(Exception e){JOptionPane.showMessageDialog(null, e);}
  }
  
  void send_msg(String msg)
  {
      try
  {
      dout.writeInt(0);
        dout.flush();
        dout.writeInt(my_id);
        dout.flush();
    dout.writeUTF(uname+":\r\n"+msg);
    dout.flush(); 
  }
  catch(Exception e)
  {
      
  }
}
  void send_image(File ff)
  {
      try
      {
       dout.writeInt(2);
        dout.flush();
        dout.writeInt(my_id);
        dout.flush();
    dout.writeUTF(uname);
    dout.flush();
InputStream fis=new FileInputStream(ff);
dout.writeUTF(ff.getName());
dout.flush();
dout.writeLong(ff.length()); 
dout.flush();
byte b[]=new byte[8192];
         int cnt;
  while( (cnt= fis.read(b))>0)
       {
           
           dout.write(b, 0, cnt);
       }
    dout.flush();
    
    fis.close();
  }
      catch(IOException e){}
  }
  void cleanClose()
  {
       try{
       
    
        if(s!=null)
        {
            if(!(s.isClosed())){
         //  JOptionPane.showMessageDialog(this, "in s");
         dout.writeInt(1);
        dout.flush();
        dout.writeInt(my_id);
        dout.flush();
    dout.writeUTF(uname+" has left the chat");
     
        }  }
        }  
        
        catch(Exception e){ JOptionPane.showMessageDialog(null,e);}
  }
}
