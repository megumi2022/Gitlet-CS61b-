import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.zip.ZipOutputStream;

public class Client extends Socket {
    static final String SERVER_IP = "127.0.0.1";//服务器端口
    static final int SERVER_PORT = 8888;//服务器IP
    static Socket s;//套接字

    /**
     * 构造函数建立连接
     */
    public Client() throws Exception {
        super(SERVER_IP, SERVER_PORT);
        this.s = this;
        System.out.println("Client[port:" + s.getLocalPort() + "]成功连接服务器。");
    }

    /**
     * 启动客户端
     * @param cmd
     * @throws Exception
     */

    public static void clientStart(String cmd) throws Exception {
        OutputStream os = s.getOutputStream();
        InputStream is = s.getInputStream();
        DataOutputStream dos = new DataOutputStream(os);
        DataInputStream dis = new DataInputStream(is);
        try {
            Client c = new Client(); //启动客户端连接
            if (cmd.equals("pull")) {//从服务器取仓库
                dos.writeUTF("get");
                c.getFile();
            } else if (cmd.equals("push")) {//把本地代码上传到服务器
                dos.writeUTF("send");
                c.sendFile();
            } else {
                System.out.println("没有该命令，远程操作失败。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接受服务器文件
     */
    public void getFile() throws Exception {
        OutputStream os = s.getOutputStream();
        InputStream is = s.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        DataOutputStream dos = new DataOutputStream(os);//接受从服务器传输来的信息
        try {
            String sign = dis.readUTF();
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入想要获取的版本名(ver1.zip)：");
            String s = sc.nextLine();
            dos.writeUTF(s);
            if ("404NOTFOUND".equals(sign)) {
                System.out.println("服务器收到客户端错误信息：文件不存在！");
                return;
            }
            if ("exist".equals(sign)) {
                if(sign.equals("success")) {
                    System.out.println("------------成功解压服务器文件至指定路径---------");
                    dos.writeUTF("success");
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 向服务器发送文件
     * @throws Exception
     */

    public void sendFile() throws Exception {//push
        try {
            OutputStream os = s.getOutputStream();
            InputStream is = s.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            DataOutputStream dos = new DataOutputStream(os);//接受从服务器传输来的信息
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入要获取的版本号（ver1，2，3.zip等)： ");
            String name = sc.nextLine();//".git.zip"
            File outPath = new File(path.getServerfile());
            FileOutputStream fos = new FileOutputStream(outPath+File.separator+name);//输出路径
            ZipOutputStream zos = new ZipOutputStream(fos);
            File git = new File(path.get_GIT());//读取源文件
            if (git.exists()) {
                dos.writeUTF("exist");
                Utils.compressFile(git, path.getServerfile(),zos);
                System.out.println("客户端收到服务器：成功push，并发送反馈");
                dos.writeUTF("success");
            } else {
                System.out.println("客户端：文件不存在，向服务器发送信息");
                dos.writeUTF("404NOTFOUND");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


