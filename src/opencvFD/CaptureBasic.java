package opencvFD;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.arcsoft.demo.AFRTest;
  
//计算速度优化 1.io优化2.计算次数优化3.线程
@SuppressWarnings("serial")
public class CaptureBasic extends JPanel {  
      
    public BufferedImage mImg;  
    
    
    private BufferedImage mat2BI(Mat mat){  
        int dataSize =mat.cols()*mat.rows()*(int)mat.elemSize();  
        
        byte[] data=new byte[dataSize];  
        mat.get(0, 0,data);  
        int type=mat.channels()==1?  
                BufferedImage.TYPE_BYTE_GRAY:BufferedImage.TYPE_3BYTE_BGR;  
          
        if(type==BufferedImage.TYPE_3BYTE_BGR){  
            for(int i=0;i<dataSize;i+=3){  
                byte blue=data[i+0];  
                data[i+0]=data[i+2];  
                data[i+2]=blue;  
            }  
        }  
        BufferedImage image=new BufferedImage(mat.cols(),mat.rows(),type);  
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);            
        return image;  
    }  
    public void paintComponent(Graphics g){  
        if(mImg!=null){  
            g.drawImage(mImg, 0, 0, mImg.getWidth(),mImg.getHeight(),this);  
        }  
    }  
      
    public static void main(String args[]) { 
    	AFRTest Fr = new AFRTest("1.JPG");
    	
        try{  
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);//opencv_java330  
            Mat capImg=new Mat();  
            VideoCapture capture=new VideoCapture(0);  
            int height = (int)capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);  
            int width = (int)capture.get(Videoio.CAP_PROP_FRAME_WIDTH);  
            if(height==0||width==0){  
                throw new Exception("camera not found!");  
            }  
            JFrame frame=new JFrame("camera");  
            
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
            CaptureBasic panel=new CaptureBasic();
       
            frame.setContentPane(panel);  
            frame.setVisible(true);  
            frame.setSize(width+frame.getInsets().left+frame.getInsets().right,  
                    height+frame.getInsets().top+frame.getInsets().bottom);  
            int n=0;
            while(frame.isShowing() && n<500){
         
                capture.read(capImg);
                panel.mImg=panel.mat2BI(detectFace(capImg));
                Fr.setImgB(panel.mImg);
                if(AFRTest.GetFr(Fr.ImgA,Fr.ImgB)>0.75) {
                    break;
                }
                panel.repaint();
                n++;
            }  
            capture.release();  
            frame.dispose();  
            Fr.DestoryAFRTest();
        }catch(Exception e){  
            System.out.println("例外：" + e);  
        }finally{  
            System.out.println("--done--");  
        }  
    }
    /**
     * opencv实现人脸识别
     * @param img
     */
    public static Mat detectFace(Mat img) throws Exception
    {

        System.out.println("Running DetectFace ... ");
        // 从配置文件lbpcascade_frontalface.xml中创建一个人脸识别器，该文件位于opencv安装目录中
        CascadeClassifier faceDetector = new CascadeClassifier("C:\\haarcascade_frontalface_alt.xml");


        // 在图片中检测人脸
        MatOfRect faceDetections = new MatOfRect();

        faceDetector.detectMultiScale(img, faceDetections);

        //System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        Rect[] rects = faceDetections.toArray();
        if(rects != null && rects.length >= 1){
        	
            for (Rect rect : rects) {  
                Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),  
                        new Scalar(0, 0, 255), 2);  
            } 
        }
        return img;
    }
    
    
    /**
     * opencv实现人型识别，hog默认的分类器。所以效果不好。
     * @param img
     */
    public static Mat detectPeople(Mat img) {
        //System.out.println("detectPeople...");
        if (img.empty()) {  
            System.out.println("image is exist");  
        }  
        HOGDescriptor hog = new HOGDescriptor();
        hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        System.out.println(HOGDescriptor.getDefaultPeopleDetector());
        //hog.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());  
        MatOfRect regions = new MatOfRect();  
        MatOfDouble foundWeights = new MatOfDouble(); 
        //System.out.println(foundWeights.toString());
        hog.detectMultiScale(img, regions, foundWeights);        
        for (Rect rect : regions.toArray()) {             
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0, 0, 255), 2);  
        }  
        return img;  
    } 
    
}