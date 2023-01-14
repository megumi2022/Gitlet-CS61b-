import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Set;

public class Tree implements Serializable {
    private static final long serialVersionUID = 2200022L;
    private TreeMap<String, String> treeConetnt;//保存之前blob的内容
    private String treeName;

    /**
     * 获取TreeName
     * @return
     */

    public String getTreeName() {
        return treeName;
    }

    /**
     * 序列化Tree
     * @param tree
     */

    public void writeToFile(Tree tree) {
        File file = new File(path.get_OBJETCS() + File.separator + getTreeName());
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tree);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置treeName
     * @throws Exception
     */
    public void setTreeName() throws Exception {
        Index index = new Index();
        TreeMap<String, String> tmap = new TreeMap<>(index.readFromFile().getIndexTmap());
        Set<String> set = tmap.keySet();
        String[] keys = set.toArray(new String[set.size()]);
        Arrays.sort(keys);
        StringBuffer content = new StringBuffer();
        for (String key : keys) {
            content.append(key);
            content.append(tmap.get(key) + " ");
        }
        //再计算content的hash值作为map对象的hash值 （tree对象的hash值可以这样计算）
        String str = content.toString();
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            this.treeName = new String(buf);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取treeContent
     * @return
     */

    public TreeMap<String, String> getTreeConetnt() {
        return treeConetnt;
    }

    /**
     * 设置treeName
     * @throws Exception
     */
    public void setTreeConetnt() throws Exception {
        Index index = new Index();
        this.treeConetnt = new TreeMap<>(index.readFromFile().getIndexTmap());
    }

    /**
     * 反序列化Tree
     * @param treePath
     * @return
     * @throws Exception
     */

    public Tree readFromFile(String treePath) throws Exception {
        Tree tree = new Tree();
        try {
            FileInputStream fis = new FileInputStream(treePath);
            ObjectInputStream ois = null;
            TreeMap<String, String> tmap;
            if (fis != null) {
                ois = new ObjectInputStream(fis);
                while (true) {
                    tree = (Tree) ois.readObject();
                }
            }else{
                getTreeConetnt().put(null,null);
            }
        }catch(EOFException ex){
                throw new EOFException(ex.getMessage());
            }finally {
            return tree;
        }

    }

    /**
     * 设置treeContet
     * @param x
     * @param y
     */
    public void setTreeConetnt(String x, String y) {
        this.treeConetnt.put(x,y);
    }

    /**
     * 重写toString
     * @return
     */
    @Override
    public String toString() {
        return "Tree{" +
                "treeConetnt=" + treeConetnt +
                ", treeName='" + treeName + '\'' +
                '}';
    }
}
