package version3.view;



import version3.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class SignupUI extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_WTDTH = 240;
    private static final int DEFAULT_HEIGHT = 150;

    private JTextField username;
    private JTextField realname;
    private JPasswordField password;
    private JButton okButton;
    private boolean ok;
    private JDialog dialog;
    private JButton cancelButton;

    private Receive receive;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private Client client;


    public SignupUI(){
        this.creatUI();
        try {
            socket = new Socket("localhost", 8888);
            objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
        }
        catch (Exception exception)
        {
            System.out.println("signup ui");
        }

        receive=new Receive(socket,this);

        new Thread(receive).start();





    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==cancelButton){
            new LoginUI();
            this.dispose();
        }else {
            String usernameText=username.getText();
            String realnameText=realname.getText();
           String passwordText= new String(password.getPassword());
            /*
            用户名、真实姓名、密码是否为空的检验
             */
            if (usernameText==""){
                JOptionPane.showMessageDialog(null, "请输入用户名！");
                return;
            }
            if (realnameText==""){
                JOptionPane.showMessageDialog(null, "请输入真实姓名！");
                return;
            }
            if (passwordText==""){
                JOptionPane.showMessageDialog(null, "请输入密码！");
                return;
            }
            client = new Client(realnameText, usernameText, passwordText);

            try{
                objectOutputStream.writeObject(client);
                objectOutputStream.flush();
            }catch (Exception exception)
            {
                System.out.println("signup actionperformed");
            }




        }
    }

    public Client getClient() {
        return client;
    }

    public static void main(String[] args)
    {

        new SignupUI();
    }
    public  void creatUI(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        setLayout(new BorderLayout());
        //construct a panel with username,real name,password
        JPanel panel=new JPanel();
        panel.setLayout(new GridLayout(3,2));
        panel.add(new JLabel("   User name:"));
        panel.add(username=new JTextField(""));
        panel.add(new Label("   Real name:"));
        panel.add(realname=new JTextField(""));
        panel.add(new JLabel("   Password:"));
        panel.add(password=new JPasswordField(""));
        //add the panel to the center of border layout.
        add(panel,BorderLayout.CENTER);

        okButton=new JButton("Sign up");

        cancelButton=new JButton("Cancel");
        //add buttons to southern border
        JPanel buttonPanel=new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel,BorderLayout.SOUTH);


        okButton.addActionListener(this);
        cancelButton.addActionListener(this);


        this.setTitle("注册窗口");
        this.setLocation(500,300);	//设置初始位置
        this.pack();  		//表示随着面板自动调整大小
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public   void enterIntoChatUI(){
        System.out.println("进入enterintochatUI");
        if (client!=null) {
            new ChatUI(client);
            this.dispose();
        }
    }



    public static class Receive implements Runnable{
        private Socket socket;
        private SignupUI signupUI;
        private ObjectInputStream objectInputStream;

        public Receive(Socket socket,SignupUI signupUI) {
            try{
                this.socket = socket;
                objectInputStream=new ObjectInputStream(socket.getInputStream());
                this.signupUI=signupUI;

            } catch (Exception e){
                System.out.println("Receive异常");
            }

        }


        @Override
        public void run() {
            //不停接收消息
            Object object=null;
            while (true){
                try {
                    String strToCheck=(String)objectInputStream.readObject();
                    if (strToCheck!=null) {
                        if (strToCheck.contains("succeed")) {
                            JOptionPane.showMessageDialog(null, "注册成功！..");

                            new ChatUI(signupUI.getClient());
                            signupUI.dispose();
                            break;
                        }
                        else if(strToCheck.contains("fail")){
                            JOptionPane.showMessageDialog(null, "抱歉，注册失败！用户ID重复");
                            continue;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
    }
}
