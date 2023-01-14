import java.io.*;
import java.util.*;

public class command {
    /**
     * 实现init命令
     *
     * @return
     * @throws Exception
     */

    protected static boolean init() throws Exception {
        try {
            //工作区中（当前路径）是否存在.git文件夹
            File file = new File(path.get_GIT());
            if (file.exists()) {
                System.out.println(".git文件夹存在");
                return true;
            } else {
                System.out.println(".git文件夹不存在，需要创建");
                file.mkdir(); //把.git当做文件夹名创建
                System.out.println(".git创建成功。开始创建objects");
                //在.git文件夹下创建一个objects文件夹用于储存blob、tree、commit等对象，命名均为其hash值
                File objects = new File(path.get_OBJETCS());
                objects.mkdir();
                System.out.println("objects创建成功。开始创建index对象。");
                //创建index对象，储存文件名与其内容hash值的对应关系，到.git目录下
                File index = new File(path.get_INDEX());
                if (index.exists()) {
                    System.out.println("index已经存在。开始创建HEAD对象。");
                } else {
                    index.createNewFile();
                    System.out.println("index对象创建成功。开始创建HEAD对象。");
                }
                //创建HEAD对象，储存最近一次的commit id（即commit对象的hash值，初始为空）
                File HEAD = new File(path.get_HEAD());
                if (HEAD.exists()) {
                    System.out.println("index已经存在。");
                } else {
                    HEAD.createNewFile();
                    System.out.println("HEAD文件创建成功。");
                }
                return true;
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    protected static boolean add(String fileName) throws Exception {
        File fileSearch = new File(path.getWorkplace() + File.separator + fileName);
        File indexFile = new File(path.get_INDEX());
        Index index = new Index();
        TreeMap<String, String> Tmap = new TreeMap<>();
        /** 文件是否存在 */
        if (!fileSearch.exists() && indexFile.length() == 0) {
            System.out.println("index暂存区为空,不需要remove");
            return true;
        }
        if (!fileSearch.exists()) {
            Tmap = (TreeMap<String, String>) index.readFromFile().getIndexTmap().clone();
            Iterator it = Tmap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
                String key = me.getKey();
                String value = me.getValue();
                if (!key.equals(fileName)) {
                    index.addIndexTmap(key, value);
                }
            }
            System.out.println("add的文件只存在于暂存区（index），而不存在于工作区时，在暂存区中删除对应条目(更新index)");
            index.writeToFile(index);//重新写入index
            return true;
        } else {
            /** 文件存在 */
            try (
                    FileInputStream fis = new FileInputStream(path.getWorkplace() + File.separator + fileName);
            ) {
                //读取该文件
                byte[] bys = new byte[4];
                StringBuffer sb = new StringBuffer();
                int b = fis.read(bys);
                while (b != -1) {
                    sb.append(new String(bys, 0, b, "UTF-8"));
                    b = fis.read(bys);
                }
                //创建Blob对象,并序列化到objects中
                Blob blob = new Blob(sb.toString());
                blob.writeToFile(blob);
                //更新index暂存区
                if (indexFile.length() == 0) {
                    index.addIndexTmap(fileName, blob.getBlobConetnt());
                } else {
                    Tmap = (TreeMap<String, String>) index.readFromFile().getIndexTmap().clone();
                    Iterator it = Tmap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
                        String key = me.getKey();
                        String value = me.getValue();
                        if (key.equals(fileName)) {
                            value = blob.getBlobConetnt();
                        }
                        index.addIndexTmap(key, value);
                    }
                    index.addIndexTmap(fileName, blob.getBlobConetnt());//加入新文件的内容
                }
                index.writeToFile(index);
            }
            System.out.println("index暂存区更新成功。");
        }
        return true;
    }

    protected static boolean addDot() throws Exception {
        //创建对象，指定路径文件
        File traverseFile = new File(path.getWorkplace());
        String fn = null;
        if (traverseFile.isDirectory()) {//判断是否有目录
            File[] flist = traverseFile.listFiles();//获取目录中的所有文件名
            if (flist == null || flist.length == 0) {
                return false;
            }
            for (int j = 0; j < flist.length; j++) { //遍历文件夹
                fn = flist[j].getName();
                try {
                    command.add(fn);//对指定路径下的文件进行遍历,并调用add方法
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        } else {
            System.out.println("错误信息：搜索的文件夹不存在！");
            return true;
        }
    }

    protected static boolean commit(String msg) throws Exception {
        //生成tree
        System.out.println("创建tree对象。");
        Tree tree = new Tree();
        tree.setTreeConetnt();
        tree.setTreeName();
        tree.writeToFile(tree);
        System.out.println("tree序列化成功");
        System.out.println("treename =" + tree.getTreeName());//t
        //生成commit文件
        System.out.println("创建commit对象");
        Commit commit = new Commit();
        commit.setParentID();
        commit.setID(tree.getTreeName());
        String cID = commit.getID();
        commit.setTime();
        commit.setMessage(msg);
        commit.setPreHead();
        LinkedList<String> pID = (LinkedList<String>) commit.getParentID().clone();
        System.out.println("pID " + pID);
        System.out.println(commit.toString());
        if (pID.size() == 0) {
            System.out.println("没有parentID,这是第一次commit,更新HEAD");
            //覆盖更新HEAD记录的commit的文件名称
            try {
                FileWriter fw = new FileWriter(path.get_HEAD(), false);
                fw.write(Utils.getsha1(commit.toString()));
                fw.flush();
                fw.close();
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            commit.writeToFile(commit);
            return true;
        } else if (cID.equals(pID.getLast())) {
            System.out.println("结构相同,不更改。");
            return true;
        } else {
            commit.writeToFile(commit);
            System.out.println("更新HEAD");
            //覆盖更新HEAD记录的commit的文件名称
            try {
                FileWriter fw = new FileWriter(path.get_HEAD(), false);
                fw.write(Utils.getsha1(commit.toString()));
                fw.flush();
                fw.close();
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            System.out.println("commit对象序列化成功,开始打印文件变动情况");
            //寻找ID对应的树,遍历文件夹
            File traverseFile = new File(path.get_OBJETCS());
            File[] flist = traverseFile.listFiles();
            if (flist == null || flist.length == 0) {
                System.out.println(path.get_OBJETCS() + " 为空，不能继续执行");
                return false;
            }
            for (int j = 0; j < flist.length; j++) {
                String fn = null;
                fn = flist[j].getName();
                if (fn.equals(pID.get(pID.size() - 1))) {
                    System.out.println(fn + " 的tree文件存在");//t
                    //parentID记录的tree存在，可以继续递归查找
                    Tree pTree = new Tree();
                    pTree = pTree.readFromFile(path.get_OBJETCS() + File.separator + pID.getLast());
                    //对比两棵树的blobcontent ,针对存在的文件
                    for (Map.Entry<String, String> pe : pTree.getTreeConetnt().entrySet()) {
                        String pkey = pe.getKey();
                        String pvalue = pe.getValue();
                        for (Map.Entry<String, String> ce : tree.getTreeConetnt().entrySet()) {
                            String key = ce.getKey();
                            String value = ce.getValue();
                            if (pkey.equals(key)) {
                                if (pvalue.length() < value.length()) {
                                    System.out.println(key + " 增加");
                                } else if (pvalue.length() > value.length()) {
                                    System.out.println(key + " 缩减");
                                } else {
                                    System.out.println(key + " 未修改");
                                }
                            }
                        }
                    }
                    //对比keyset
                    List<String> cList = new ArrayList<>(tree.getTreeConetnt().keySet());
                    List<String> pList = new ArrayList<>(pTree.getTreeConetnt().keySet());
                    List<String> tempList = new ArrayList<>(pList);
                    //以前的有，现在的没有，打印“删除”
                    pList.removeAll(cList);//求差集
                    System.out.println(pList + " 文件已删除。");
                    cList.removeAll(tempList);
                    System.out.println(cList + " 文件新增。");
                }
            }
        }
        return true;
    }

    protected static boolean rm(String fileName) throws Exception {
        boolean res = false;
        res = rmc(fileName);
        System.out.println("开始删除文件");
        //创建对象，指定路径文件
        File traverseFile = new File(path.getWorkplace());
        File rmFile = new File(path.getWorkplace() + File.separator + fileName);
        //判断是否有目录
        if (traverseFile.isDirectory()) {
            //获取目录中的所有文件名
            String[] fn = traverseFile.list((dir, name) -> name.endsWith(".txt"));
            //对指定路径下的文件进行遍历,并调用add方法
            Arrays.stream(fn).forEach(c -> {
                try {
                    if (c.equals(fileName)) {
                        rmFile.delete();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        }
        System.out.println(fileName + "从工作区删除成功");
        return true;
    }

    protected static boolean rmc(String fileName) throws Exception {
        Index index = new Index();
        TreeMap<String, String> Tmap = new TreeMap<>();
        Tmap = (TreeMap<String, String>) index.readFromFile().getIndexTmap().clone();
        Iterator it = Tmap.entrySet().iterator();
        String key = null;
        String value = null;
        while (it.hasNext()) {
            Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
            key = me.getKey();
            value = me.getValue();
            if (!key.equals(fileName)) {
                index.addIndexTmap(key, value);
            }
        }
        index.writeToFile(index);
        System.out.println("已经删除index暂存区的" + fileName);
        return true;
    }

    protected static boolean log() throws Exception {
        Commit commit = new Commit();
        commit.setPreHead();
        //读取HEAD文件里的commit，最新版
        FileReader fr = new FileReader(path.get_HEAD());
        BufferedReader br = new BufferedReader(fr);
        String h = br.readLine();
        if (commit.getPreHead().size() == 0) {
            System.out.println("HEAD为空");
        } else {
            LinkedList<String> pHead = (LinkedList<String>) commit.getPreHead().clone();
            if (!pHead.isEmpty()) {
                File file = new File(path.get_OBJETCS());
                File[] flist = file.listFiles();
                if (flist == null || flist.length == 0) {
                    return false;
                }
                for (int j = 0; j < flist.length; j++) { //遍历objects文件夹
                    String fn = null;
                    fn = flist[j].getName();
                    for (int i = 0; i < pHead.size(); i++) {
                        if (fn.contains(pHead.get(i)) || fn.contains(h)) {
                            Commit tempCommit = commit.readFromFile(fn);
                            System.out.println("-------------打印备注为 " + tempCommit.getMessage() + " 的commit---------");
                            System.out.println(tempCommit);
                        }
                    }
                }
            }
        }
        return true;
    }

    protected static boolean resetSoft(String name) throws Exception {
        FileWriter head = new FileWriter(path.get_HEAD(), false);
        head.write(name);
        head.flush();
        head.close();
        return true;
    }

    protected static boolean resetMixed(String name) throws Exception {
        boolean res = resetSoft(name);
        //创建对象，指定路径文件
        File traverseFile = new File(path.get_OBJETCS());
        File commitFile = new File(path.getWorkplace() + File.separator + name);
        File[] flist = traverseFile.listFiles();
        if (flist == null || flist.length == 0) {
            res = false;
            return res;
        }
        for (int j = 0; j < flist.length; j++) {
            String fn = null;
            fn = flist[j].getName();
            if (fn.equals(name)) {
                System.out.println(name + " 的commit文件存在");
                Commit commit = new Commit();
                commit = commit.readFromFile(name);
                String ctree = commit.getID();
                //遍历objects寻找目标commit对应的tree
                for (int y = 0; y < flist.length; y++) {
                    String fnn = flist[y].getName();
                    if (fnn.equals(ctree)) {//不可以用“==”
                        Tree tree = new Tree();
                        tree = tree.readFromFile(path.get_OBJETCS() + File.separator + ctree);
                        Index index = new Index();
                        index.setIndexTmap(tree.getTreeConetnt());
                        index.writeToFile(index);
                        System.out.println("index暂存区恢复成功");
                        break;
                    }
                }
            }
        }
        return res;
    }

    protected static boolean resetHard(String name) throws Exception {
        boolean res = resetMixed(name);
        Index index = new Index();
        index = index.readFromFile();
        TreeMap<String, String> Tmap = new TreeMap<>(index.getIndexTmap());
        //遍历工作区的文件
        //创建对象，指定路径文件
        File traverseFile = new File(path.getWorkplace());
        File[] flist = traverseFile.listFiles();
        if (flist == null || flist.length == 0) {
            res = false;
            return res;
        }
        //求差集
        ArrayList<String> fnList = new ArrayList<String>();
        for (int i = 0; i < flist.length; i++) {
            String fn = flist[i].getName();
            fnList.add(fn);
            for (Map.Entry<String, String> en : Tmap.entrySet()) {
                String key = en.getKey();
                String value = en.getValue();
                if (fn.equals(key)) {
                    FileWriter fw = new FileWriter(path.getWorkplace() + File.separator + fn, false);
                    System.out.println(fn + " 已经存在，更新内容");
                    fw.write(value);
                    fw.flush();
                    System.out.println(fn + " 已经更新完成");
                }
                if (i == flist.length - 1) break;
            }
        }
        ArrayList<String> tempList = new ArrayList<>(fnList);
        fnList.removeAll(Tmap.keySet());
        if (fnList.size() != 0) {//存在多余的文件，删除
            for (int i = 0; i < fnList.size(); i++) {
                File deleteFile = new File(path.getWorkplace() + File.separator + fnList.get(i));
                deleteFile.delete();
                System.out.println("多余文件 " + deleteFile + "已经删除");
            }
        }

        Tmap.remove(tempList);
        if (!Tmap.isEmpty()) {//存在没有的文件，恢复
            Iterator it = Tmap.entrySet().iterator();
            Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
            while (it.hasNext()) {
                String key = me.getKey();
                String value = me.getValue();
                FileWriter fw = new FileWriter(path.getWorkplace() + File.separator + key, false);
                fw.write(value);
                fw.flush();
            }
        }
        return res;
    }

    /**
     * 把git传送到服务器
     *
     * @return
     * @throws Exception
     */
    protected static boolean pull() throws Exception {
        Client client = new Client();
        client.clientStart("pull");
        return true;
    }

    protected static boolean push() throws Exception {
        Client client = new Client();
        client.clientStart("push");
        return true;
    }
}
