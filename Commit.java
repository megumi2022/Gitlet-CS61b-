//将index中所有条目生成tree对象序列化到objects文件夹下；
//打印本次commit相对上一次commit的文件变动情况（增加、删除、修改）
//将commit对象序列化到objects文件夹下，commit对象包括以下属性：上一次的commit id、本次commit所生成tree对象id，message、commit时间
//更新HEAD文件中的commit id。

import java.io.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

public class Commit implements Serializable,Cloneable{
    private LinkedList<String> parentID;//根树的文件名
    private String ID;//当前树的文件名
    private LinkedList<String> preHead; //上一个commit的文件名
    private String message;//备注
    private String time;//提交时间

    /**
     * 获取上一个commit的文件名
     * @return
     */
    public LinkedList<String> getPreHead() {
        return preHead;
    }

    /**
     * 设置preHead
     * @throws IOException
     */
    public void setPreHead() throws IOException{
        this.preHead = new LinkedList<>();//必须初始化，否则会出现NULLPOINTEREXCEPTION
        try {
            FileReader fr = new FileReader(path.get_HEAD());
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line = null;
            line = br.readLine();
            while(line != null ){
                sb.append(line);
                line = br.readLine();
            }
            if(String.valueOf(sb).length() != 0) {
                Commit tempCommit = readFromFile(String.valueOf(sb));
                this.preHead.addAll(tempCommit.getPreHead());
                this.preHead.add(String.valueOf(sb));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取根树ID
     * @return
     * @throws Exception
     */

    public LinkedList<String> getParentID() throws Exception{
        return parentID;
    }

    /**
     * 设置ParentID，从HEAD读入
     * @throws IOException
     */

    public void setParentID() throws IOException{//这是之前的根树的ID，从HEAD读入上次的commit,再读入上次的根树ID和PID
        this.parentID = new LinkedList<>();//必须初始化，否则会出现NULLPOINTEREXCEPTION
        try {
            FileReader fr = new FileReader(path.get_HEAD());
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line = null;
            line = br.readLine();
            while(line != null ){
                sb.append(line);
                line = br.readLine();
            }
            if(String.valueOf(sb).length() != 0) {
                Commit tempCommit = readFromFile(String.valueOf(sb));
                this.parentID.addAll(tempCommit.getParentID());
                this.parentID.add(tempCommit.getID());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取ID
     * @return
     */

    public String getID() {
        return ID;
    }

    /**
     * 设置ID
     * @param ID
     * @throws Exception
     */

    public void setID(String ID) throws Exception{//本次commit所生成tree对象id
        this.ID = ID;
    }

    /**
     * 获取Message
     * @return
     */

    public String getMessage() {
        return message;
    }

    /**
     * 设置备注
     * @param msg
     */

    public void setMessage(String msg) {
        this.message = msg;
    }

    /**
     * 获取提交时间
     * @return
     */

    public String getTime() {
        setTime();
        return time;
    }

    /**
     * 设置提交时间
     */

    public void setTime() {
        Calendar calendar = Calendar.getInstance(); // get current instance of the calendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        this.time = formatter.format(calendar.getTime());
    }

    /**
     * 序列化Commit
     * @param commit
     */

    public void writeToFile(Commit commit) {
        String cName = Utils.getsha1(toString());
        File file = new File(path.get_OBJETCS() + File.separator + cName);
        try {
            FileOutputStream fos = new FileOutputStream(file,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(commit);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化Commit
     * @param name
     * @return
     * @throws Exception
     */

    public Commit readFromFile(String name) throws Exception{
        Commit commit = new Commit();
        try {
            FileInputStream fis = new FileInputStream(path.get_OBJETCS()+File.separator+name);
            ObjectInputStream ois = null;
            if(fis != null) {
                ois = new ObjectInputStream(fis);
                while(true) {
                    commit = (Commit) ois.readObject();
                }
            }
        } catch (EOFException ex) {
            throw new EOFException();
        }finally {
            return commit;
        }
    }

    @Override
    public String toString() {
        return "Commit{" +
                "parentID=" + parentID +
                ", ID='" + ID + '\'' +
                ", preHead=" + preHead +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
