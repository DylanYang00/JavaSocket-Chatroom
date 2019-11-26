package version3.Server;

import version3.Client;
import version3.util.Utility;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server  {
    private static CopyOnWriteArrayList<Channel> channels=new CopyOnWriteArrayList<Channel>();

    /**
     * 运行时的主方法
     */
    public static void main(String[] args) {

        System.out.println("-----Server------");

        try {
            //指定端口 创建serverSocket
            ServerSocket serverSocket=new ServerSocket(8888);
            //阻塞式等待连接
            while (true){
                Socket socket=serverSocket.accept();
                System.out.println("一个客户建立了连接"+socket.getInetAddress());
                Channel channel=new Channel(socket);
                channels.add(channel);
                new Thread(channel).start();
            }
        } catch (IOException e) {
            System.err.println("Server  main()");

        }



    }
    public static void sendToAll(Client client){
        for (Channel channel:channels) {
            channel.send(client);
        }
        String message=client.getMessage();
                message=null;
    }


    /**
     * channel类是通道类，一个channel对象代表和一个用户沟通所需要的一对通道
     * 一、实现的主要功能：
     * 1. 接收消息
     * 2. 发送消息
     * 3. 释放资源
     * 二、多线程中的应用：
     * 一个线程用一个channel对象创建，这个channel对象的run方法是不断的收发消息
     */
    public static class Channel implements Runnable,Closeable{
        private ObjectInputStream objectInputStream=null;
        private ObjectOutputStream objectOutputStream=null;
        private Socket serverSocket;
        private Client clientToSend=null;
        private boolean isRunning;
        public Channel(Socket serverSocket) {
            this.serverSocket = serverSocket;
            try {
                objectInputStream=new ObjectInputStream(serverSocket.getInputStream());
                objectOutputStream=new ObjectOutputStream(serverSocket.getOutputStream());
            }catch (Exception exception){

            }
            isRunning=true;
        }

        /**
         * 接收到对象后让服务器将该对象转发给所有人
         * @return 服务器群发的对象
         */
        public Client getClientToSend() {
            return clientToSend;
        }

        public Client receive(){
            Client client=null;
            try {
                client=(Client) objectInputStream.readObject();
            }catch (Exception exception){
                System.err.println("Channel receive()");

            }

            return client;
        }
        public void send(Client client){
            try {
                if (client!=null){
                    objectOutputStream.writeObject(client);
                    objectOutputStream.flush();

                }
            }catch (Exception e){
                e.printStackTrace();

            }


        }


        /**
         * 目前的版本仅仅是服务器接收到消息即发回，以后会改成群发
         */
        @Override
        public void run() {
            System.out.println("线程开始运行");
            while (isRunning){
                Client client=receive();
                System.out.println("receive到了");
                if (client!=null){
                    System.out.println("channel接收到了！");
                    clientToSend=client;
                    if (clientToSend.getMessage()==null)//不带信息 那要么是注册要么是登录
                    {
                        if (clientToSend.getName()==null)//没输真实姓名意味着当前是在登录
                        {
                            /*
                            登录检查代码
                             */
                            login(clientToSend);
                        }
                        else {
                            /*
                            注册代码
                             */
                            signUp(clientToSend);
                        }
                    }else{//此时是要群发信息
                        System.out.println(clientToSend.getUserID()+clientToSend.getMessage());
                        sendToAll(clientToSend);
                    }

                }

            }
        }

        @Override
        public void close() throws IOException {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
            objectOutputStream.close();
            }
            if (serverSocket!=null){
                serverSocket.close();
            }
            isRunning=false;
            channels.remove(this);
        }

        public  void signUp(Client client){
            File file=Utility.getDesktopAddress();
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            /*
            检查是否用户名重叠
             */
                FileInputStream fileInputStream=new FileInputStream(file);
                ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);

                Client clientToCheck=null;
                while( (clientToCheck=(Client)objectInputStream.readObject())!=null)
                {
                    if (clientToCheck.getUserID().equals(client.getUserID()))
                    {
                   /*
                   发送提示：注册失败，用户ID重复
                    */
                        objectOutputStream.writeObject("fail");
                        objectOutputStream.flush();
                        return;
                    }
                }


            }catch (Exception e){
                e.printStackTrace();
            }
            /*
            进行注册
             */
            try {
            FileOutputStream fileOutputStream=new FileOutputStream(file,true);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(client);
            objectOutputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
            /*
            发送提示：注册成功！
             */
            try {
            objectOutputStream.writeObject("succeed");
            objectOutputStream.flush();
            this.close();
            return;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public  void login(Client client){
            File file=Utility.getDesktopAddress();
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            /*
            检查用户名密码是否匹配
             */
                FileInputStream fileInputStream=new FileInputStream(file);
                ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);


                Client clientToCheck=null;
                while( (clientToCheck=(Client)objectInputStream.readObject())!=null)
                {
                    if (clientToCheck.getUserID().equals(client.getUserID())&&client.getPassword().equals(clientToCheck.getPassword()))
                    {
                    /*
                    通知登录成功
                     */
                        objectOutputStream.writeObject("succeed");
                        objectOutputStream.flush();
                        this.close();
                        return;
                    }
                }

        /*
        通知该用户尚未注册，请先注册
         */
            objectOutputStream.writeObject("fail");
            objectOutputStream.flush();
            }catch (Exception e){
               e.printStackTrace();
            }
        }

    }
}
