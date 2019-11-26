package version3.Server;

import version3.Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerTest {

    public static void main(String[] args) throws Exception{
        Socket socket=new Socket("localhost",8888);
        Client client=new Client("YangYuchen","a1392202624","yyc123tab");


        ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
        Client newClient;
        while (true){
            if (client!=null)
            {

                System.out.println("已发送对象");
            }
            if ((newClient=(Client)objectInputStream.readObject())!=null){
                System.out.println("成功接收对象");
                System.out.println(newClient.getName()+newClient.getPassword());
            }
        }
    }

}


