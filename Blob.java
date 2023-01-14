
import java.io.*;
import java.security.MessageDigest;

public class Blob implements Serializable {
    private static final long serialVersionUID = 1L;
    private String blobName;//序列化Blob的文件名
    private String blobConetnt;//文件内容的SHA-1
    private String type = "Blob";//类型

    /**
     * 有参构造
     * @param blobConetnt
     */

    public Blob(String blobConetnt) {
        this.blobConetnt = blobConetnt;
        setBlobName();
    }

    /**
     * 获取blobName
     * @return
     */

    public String getBlobName() {
        return blobName;
    }

    /**
     * 设置blobName
     */

    public void setBlobName() {
        this.blobName = Utils.getsha1(getBlobConetnt());
    }

    /**
     * 获取blobContent
     * @return
     */
    public String getBlobConetnt() {
        return blobConetnt;
    }

    /**
     *设置blobContent
     * @param blobConetnt
     */

    public void setBlobConetnt(String blobConetnt) {
        this.blobConetnt = blobConetnt;
    }

    /**
     * 重写toString
     * @return
     */
    @Override
    public String toString() {
        return "Blob{" +
                "blobConetnt='" + blobConetnt + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    /**
     * 序列化Blob
     * @param blob
     */
    public void writeToFile(Blob blob){
        File file = new File(path.get_OBJETCS()+ File.separator+getBlobName());
        try{
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(blob);
                oos.flush();
                oos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
