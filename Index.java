import java.io.*;
import java.util.TreeMap;

public class Index implements Serializable,Cloneable{
    private static final long serializableUID = 1L;
    private TreeMap<String, String> indexTmap;//key是blob的sha1,也就是它的名字,内容是blobcontent

    /**
     * 获取IndexTmap
     * @return
     * @throws Exception
     */

    public TreeMap<String, String> getIndexTmap()throws Exception {
        return indexTmap;
    }

    /**
     * 设置IndexTmap
     * @param indexTmap
     */
    public void setIndexTmap(TreeMap<String, String> indexTmap) {
        this.indexTmap = (TreeMap<String, String>) indexTmap.clone();
    }

    /**
     * 更新IndexTmap
     * @param fileName
     * @param content
     */
    public void addIndexTmap(String fileName, String content) {
        this.indexTmap = new TreeMap<>();
        this.indexTmap.put(fileName, content);
    }

    /**
     * 反序列化Index
     * @return
     * @throws Exception
     */

    public Index readFromFile() throws Exception {
        Index index = new Index();
        try {
            FileInputStream fis = new FileInputStream(path.get_INDEX());
            ObjectInputStream ois = null;
            if(fis != null) {
                ois = new ObjectInputStream(fis);
                while(true) {
                    index = (Index) ois.readObject();
                }
            }
            ois.close();
        } catch (EOFException ex) {
            throw new EOFException();
        }finally {
            return index;
        }
    }

    /**
     * 序列化Index
     * @param index
     * @throws Exception
     */

    public void writeToFile(Index index)throws Exception{
        try{
            FileOutputStream fos = new FileOutputStream(path.get_INDEX(),false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(index);
            oos.flush();
            oos.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 重写toString
     * @return
     */

    @Override
    public String toString() {
        return "Index{" +
                "indexTmap=" + indexTmap +
                '}';
    }
}
