package version3.view;





import version3.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ChatUI extends JFrame implements ActionListener {
    /**
     * 界面属性相关的属性
     */
    private static JTextArea chatTextArea;
    private static JTextField sendField;
    private JButton sendButton;
    private Client client;
    /**
     * 通信相关的属性成员
     * send 和receive各代表一个输入输出管道
     */
    private Socket socket;
    private Send send;
    private Receive receive;
    public ChatUI(Client client){
        try {
            creatUI();
            socket=new Socket("localhost",8888);
            this.client=client;
            send=new Send(socket,client);
            receive=new Receive(socket);

            new Thread(receive).start();

        }catch (Exception e)
        {
            System.out.println("ChatUI异常");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message=sendField.getText();

        client.setMessage(message);

        send.send(client);
       client.setMessage(null);
        sendField.setText("");
    }
    public static void  addToArea(Client client){
        chatTextArea.append(client.getUserID()+System.lineSeparator());
        chatTextArea.append(client.getMessage()+System.lineSeparator());
    }
    public void creatUI(){
        setTitle("Chat Interface V1.0");
        setSize(240,150);
        setLocation(500,300);
        JPanel centerPanel=new JPanel();
        JPanel southPanel=new JPanel();
        setLayout(new BorderLayout());

        chatTextArea=new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        sendField=new JTextField(20);
        sendButton=new JButton("Send");

        centerPanel.add(chatTextArea);
        southPanel.add(sendField);
        southPanel.add(sendButton);
        add(centerPanel,BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(this);

        this.pack();  		//表示随着面板自动调整大小
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    public static class Receive implements Runnable{
        private Socket socket;

        private ObjectInputStream objectInputStream;

        public Receive(Socket socket) {
           try{
               this.socket = socket;
               objectInputStream=new ObjectInputStream(socket.getInputStream());

           } catch (Exception e){
               e.printStackTrace();
           }

        }


        @Override
        public void run() {
            //不停接收消息
            Object object=null;
            while (true){
                try {
                    object=objectInputStream.readObject();

                        addToArea((Client) object);
                        continue;

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
    }

    public static class Send {
        ObjectOutputStream objectOutputStream=null;
        Socket socket;
        Client client;
        public Send(Socket socket,Client client) {
            this.socket = socket;
            this.client = client;
            try {
                objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            }catch (Exception e){
                System.out.println("Send类异常");
            }
        }
        public  void send(Client client){
            try {
                objectOutputStream.reset();
                objectOutputStream.writeObject(client);
                objectOutputStream.flush();
            } catch (IOException e) {
                System.out.println("客户端send出现问题");
            }

        }
       /* @Override
        public void run() {
            while (true){
                //从输出得到用户对象
                if (client!=null) {
                    send(client);
                    client = null;
                }
            }
        }*/
    }
}
