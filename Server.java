import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends ServerSocket {
    static final int SERVER_PORT = 8888;//服务器端口

    /**
     * 构造
     * @throws IOException
     */

    public Server() throws IOException {
        super(SERVER_PORT);
    }

    /**
     * 主函数
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            //启动服务端
            Server server = new Server();
            server.load();
            System.out.println("-----------服务器启动，等待客户端连接--------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用线程处理每个客户端的传输文件
     * @throws Exception
     */
    public void load() throws Exception {
        while (true) {
            //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket s = this.accept();
            //每收到一个socket就建立一个新的线程来处理它
            new Thread(new Task(s)).start();
        }
    }

    static class Task implements Runnable {
        private final Socket s;

        public Task(Socket s) {
            this.s = s;
        }

        /**
         * 重写run()
         */
        @Override
        public void run() {
            /*1、定义实现Runnable接口的类，并实现该类中的run（）方法。
            2、建立一个Thread对象，并将实现的Runnable接口的类的对象作为参数传入Thread类的构造方法。
            3、通过Thread类中的start()方法启动线程，并运行。*/
            try {
                InputStream is = s.getInputStream();
                OutputStream os = s.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                DataInputStream dis = new DataInputStream(is);
                //判断执行什么操作
                String sign = dis.readUTF();
                if (sign.equals("get")) {//读取信号，从服务器取文件
                    //取文件,pull
                    String targetFile = sign;//获取压缩的文件名".git.zip"
                    File file = new File(path.getServerfile()+File.separator+targetFile);
                    if (file.exists()) {
                        System.out.println("-------文件存在，开始传输给客户端--------");
                        //删除原来的.git文件
                        File git = new File(path.get_GIT());
                        if (git.exists()) {
                            git.delete();
                        }
                        //解压服务器文件
                        dos.writeUTF("exist");//给客户端发送可以传输的指令
                        Utils.readZip(file);
                        dos.writeUTF("success");//给客户端发送成功提示
                        if (sign.equals("success")) {
                            System.out.println("服务器接收客户端信息：客户端成功接收---------");//接收客户端反馈，并打印
                        }
                    } else {
                        //如果不存在这个文件，服务器发送文件不存在的错误信息到客户端
                        System.out.println("发送错误信息：文件不存在！");
                        dos.writeUTF("404NOTFOUND");
                        dos.flush();
                    }
                }
                if (sign.equals("send")) {
                    //发文件push，服务器准备接收
                    System.out.println("服务器：收到指令，等待push");
                    if(sign.equals(null)){
                        System.out.println("服务器：没有接收到命令，报错");
                        return;
                    }
                    if (sign.equals("exist")) {
                        System.out.println("服务器收到客户端消息：文件存在，可以发送");
                        if (sign.equals("success")) {
                            System.out.println("收到客户端消息：成功上传至服务器");
                            return;
                        }
                    }
                }
                    if(sign.equals("404NOTFOUND")) {
                        System.out.println("服务器收到客户端：文件不存在");
                        return;
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}