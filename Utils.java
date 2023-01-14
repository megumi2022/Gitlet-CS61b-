import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Utils {
    /**
     * 计算SHA-1
     * @param str
     * @return
     */
    public static String getsha1(String str) {
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
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解压zip文件到指定目录
     * @param targetFile
     * @throws IOException
     */
    public static void readZip(File targetFile) throws IOException {
        System.out.println("开始解压");
        try (FileInputStream fis = new FileInputStream(targetFile);
             ZipInputStream zis =
                     new ZipInputStream(new BufferedInputStream(fis))) {
            ZipEntry entry;
            // 从ZipInputStream读取每个条目，直到没有
            // 发现更多条目，返回值为空
            // getNextEntry()方法。
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("解压: " + entry.getName());
                int size;
                byte[] buffer = new byte[2048];
                File fileOut = new File(path.getGitlet());//指定解压路径
                try (FileOutputStream fos =
                             new FileOutputStream(fileOut);
                     BufferedOutputStream bos =
                             new BufferedOutputStream(fos, buffer.length)) {

                    while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, size);
                    }
                    bos.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 迭代方式进行文件压缩
     * @param file
     * @param fileName
     * @param outputStream
     * @throws IOException
     */
    public static void compressFile(File file, String fileName, final ZipOutputStream outputStream) throws IOException {
        //如果是目录
        if (file.isDirectory()) {
            //创建文件夹
            outputStream.putNextEntry(new ZipEntry(fileName+File.separator));
            //迭代判断，并且加入对应文件路径
            File[] files = file.listFiles();
            Iterator<File> iterator = Arrays.asList(files).iterator();
            while (iterator.hasNext()) {
                File f = iterator.next();
                compressFile(f, fileName+File.separator+f.getName(), outputStream);
            }
        } else {
            //创建文件
            outputStream.putNextEntry(new ZipEntry(fileName));
            //读取文件并写出
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            byte[] bytes = new byte[1024];
            int n;
            while ((n = bufferedInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, n);
            }
            //关闭流
            fileInputStream.close();
            bufferedInputStream.close();
        }
    }




}
