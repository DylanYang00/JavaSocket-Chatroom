package version3.view;

import version3.Client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

public class LoginUI extends JFrame implements ActionListener
{
    JButton login = new JButton("登录");
    JButton exit = new JButton("退出");
    JLabel  name = new JLabel("用户名：");
    JLabel password = new JLabel("密码：");
    JTextField JName = new JTextField(10); //明文账号输入
    JPasswordField JPassword = new JPasswordField(10); // 非明文密码输入；
    JButton signUp=new JButton("注册");

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    Client client;
    public LoginUI()
    {
        creatUI();
        try{
            socket=new Socket("localhost",8888);
            objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
        }catch (Exception e){
            System.err.println("login socket");
        }
        Receive receive=new Receive(socket,this);
        new Thread(receive).start();


    }
    public void actionPerformed(ActionEvent e)  // 对事件进行处理
    {
        if(e.getSource() == exit)
        {
            int i = JOptionPane.showConfirmDialog(null,"确认要退出吗？", "确认", JOptionPane.YES_NO_OPTION);
            // 显示选择对话框
            if(i == JOptionPane.YES_OPTION);
            System.exit(0);
        }
        else if (e.getSource()==login)
        {
            String username=JName.getText();
            String password= new String(JPassword.getPassword());
            client=new Client(username,password);
            try {
                objectOutputStream.writeObject(client);
                objectOutputStream.flush();
            }catch (Exception exc)
            {
                System.err.println("loginUI login");;
            }


        }
        else {
            new SignupUI();
            this.dispose();
        }
    }

    /**
     * 为登录页面初始化UI界面，这是构造方法实际上执行的。
     */
    void creatUI(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        setLayout(new BorderLayout());
        JPanel jp = new JPanel();
        //jp2 is used to deposit 3 buttons
        JPanel jp2=new JPanel();
        jp.setLayout(new GridLayout(2,2));  //2行2列的面板jp（网格布局）

        name.setHorizontalAlignment(SwingConstants.RIGHT);  //设置该组件的对齐方式为向右对齐
        password.setHorizontalAlignment(SwingConstants.RIGHT);

        jp.add(name);   //将内容加到面板jp上
        jp.add(JName);
        jp.add(password);
        jp.add(JPassword);

        add(jp,BorderLayout.CENTER);//将整块表单面板定义在中间

        jp2.add(login);
        jp2.add(exit);
        jp2.add(signUp);
        add(jp2,BorderLayout.SOUTH);//将按钮面板定义在南部
        login.addActionListener(this); //登录增加事件监听
        exit.addActionListener(this);	//退出增加事件监听
        signUp.addActionListener(this);//注册事件监听


        this.setTitle("登录窗口");
        this.setLocation(500,300);	//设置初始位置
        this.pack();  		//表示随着面板自动调整大小
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args)
    {

        new LoginUI();
    }
    public static class Receive implements Runnable{
        private Socket socket;

        private ObjectInputStream objectInputStream;
        LoginUI loginUI;

        public Receive(Socket socket,LoginUI loginUI) {
            try{
                this.socket = socket;
                objectInputStream=new ObjectInputStream(socket.getInputStream());

            } catch (Exception e){
                e.printStackTrace();
            }
        this.loginUI=loginUI;
        }


        @Override
        public void run() {
            //不停接收消息
            while (true){
                try {


                    String strToCheck =(String)objectInputStream.readObject();


                    if (strToCheck!=null) {
                        if (strToCheck.contains("succeed")) {
                            JOptionPane.showMessageDialog(null, "登录成功！..");
                            new ChatUI(this.loginUI.client);
                            this.loginUI.dispose();


                            break;
                        }
                        else if(strToCheck.contains("fail")){
                            JOptionPane.showMessageDialog(null, "抱歉，登录失败！没有响应的用户ID");
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



