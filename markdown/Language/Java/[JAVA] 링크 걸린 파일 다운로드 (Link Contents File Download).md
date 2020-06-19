## [JAVA] 링크 걸린 파일 다운로드 (Link Contents File Download)

웹서버 경로만 알면 해당 경로로 액세스되는 모든 스트림을 다운 받음
텍스트, 동영상 가리지 않고 다운받는다.
해당 링크에서 얻을 수 있는 파일의 확장자는 직접 지정해 줘야 함.
링크 배열에 넣은 갯수만큼 멀티스레드로 동작.

나중 크롤링 등 다른 사용처에 쓸 수 있을 것 같다.

```java
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * FileReceiver class
 * sunghs.tistory.com
 */
public class FileReceiver extends Thread {
     
    String root;
     
    public FileReceiver(String root) {
        this.root = root;
    }
     
    @Override
    public void run() {
         
        try {
             
            URL url = this.getUrl(root);
            String path = "D:/";
                String name = String.valueOf(System.currentTimeMillis());
                String ext = ".txt";
             
                boolean result = this.download(url, path, name, ext);
             
                if(result) System.out.println(path + name + ext + " .. Downloads Complete");
                else System.out.println(path + name + ext + " .. Downloads Fail");
        }
         
        catch(Exception ex) {
             
            System.out.println("Fatal Error");
            ex.printStackTrace();
        }
    }
     
    /**
     * @param root 경로 (https://www.google.com)
     * @return URL
     * @throws Exception
     */
    public URL getUrl(String root) throws Exception {
         
        if(root == null || root.equals("")) {
            throw new Exception("download root is invalid");
        }
         
        return new URL(root);
    }
     
    /**
     * @param url
     * @param path 경로 (D:/)
     * @param name 파일명 timeStamp
     * @param ext 확장자 (.txt, .mp4)
     * @return download result
     * @throws Exception
     */
    public boolean download(URL url, String path, String name, String ext) throws Exception {
         
        boolean downloadResult = false;
         
        if(path == null) path = "";
        if(name == null) name = "";
        if(ext == null) ext = "";
         
        String file = path + name + ext;
         
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        InputStream inputStream = url.openStream();
         
        int MB = 1024 * 1024;
         
        byte[] buff = new byte[MB];
        float buffSize = 0;
        float downloadSize = 0;
         
        DecimalFormat format = new DecimalFormat("#.###");
         
        while ((buffSize = inputStream.read(buff)) > 0) {
            System.out.flush();
            downloadSize = downloadSize + (buffSize / MB);
            System.out.println(name + ext + " ... downloadSize : " + format.format(downloadSize) + " MB" );
            fileOutputStream.write(buff, 0, (int)buffSize);
        }
         
        fileOutputStream.close();
        inputStream.close();
        downloadResult = true;
         
        return downloadResult;
    }
 
    public static void main(String[] args) throws Exception {
 
        String[] site = {
                "https://www.google.com",
                "https://www.naver.com"
        };
         
        for(int i = 0; i < site.length; i ++) {
            new FileReceiver(site[i]).start();
            Thread.sleep(100);
        }
    }
 
}
```